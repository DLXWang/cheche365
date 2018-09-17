package com.cheche365.cheche.core.serializer

/**
 * Created by zhengwei on 5/9/16. <br>
 * 根据配置，把java对象copy到map中。主要解决系统中的model和前端json结构的映射关系，具体包括以下问题：<br>
 * 1. 前端需要多个对象组合而成的结果。新建个组合的对象太麻烦，也不灵活，可以根据本类，基于配置生成组合结果 <br>
 * 2. 过滤掉多余的属性。JPA和jackson也能实现类似功能，但都很麻烦，jackson也不能过滤掉嵌套对象的属性 <br>
 * 3. 修改属性的名字。可以通过类似'partnerUser.partnerId->uid'，修改对象中属性的名字 <br>
 *
 * 配置：
 *     [
 *        'sourcePath':['payments'],
 *        'targetPath': 'purchaseOrder.payments',
 *        'fields': ['id']
 *    ]
 * 解释：
 * 把java对象payments的copy到map的purchaseOrder结构下的payments结构下，只处理id属性
 * 比如java对象如下：
 * {
 *     'payments' : [
 *          {
 *              'id' : 1,
 *              'amount' : 20
 *          },
 *          {
 *              'id' : 2,
 *              'amount' : 30
 *          }
 *
 *     ]
 * }
 *
 * 处理后的结果为：
 * {
 *     'purchaseOrder' : {
 *         'payments' : [
 *              {
 *                  'id' : 1,
 *              },
 *              {
 *                  'id' : 2
 *              }
 *         ]
 *     }
 * }
 */
abstract class ModelFieldsCopier extends HashMap {

    def copyFields(bills){

        fieldsMapping().each{ entry ->
            entry.sourcePath.each { singleSourcePath ->
                def sourceObj = bills

                if(singleSourcePath){  //先找到sourcePath中指定的对象
                    singleSourcePath.split('\\.').each{ pathSegment ->
                        sourceObj = sourceObj && sourceObj[pathSegment] ? sourceObj[pathSegment] : null
                    }
                }


                if(sourceObj){

                    def targetObj = this
                    if(entry.targetPath){  //targetPath为空则默认copy到this上
                        def targetPathSegments = entry.targetPath.split('\\.')
                        targetPathSegments.eachWithIndex{ pathSegment, i ->
                            if(!targetObj[pathSegment]){
                                if(sourceObj instanceof List && i==targetPathSegments.size()-1){
                                    targetObj[pathSegment] = new ArrayList()
                                } else {
                                    targetObj[pathSegment] = new HashMap()
                                }
                            }
                            targetObj = targetObj[pathSegment]
                        }
                    }

                    if(sourceObj instanceof List){
                        sourceObj.eachWithIndex{ sourceObjItem, i ->
                            targetObj[i] = new HashMap()
                            entry.fields.each { field -> handleFieldsPath(targetObj[i], sourceObj[i], field) }
                        }
                    } else {
                        entry.fields.each { field -> handleFieldsPath(targetObj, sourceObj, field) }
                    }

                }
            }
        }
    }

    def abstract fieldsMapping()

    /**
     * 处理待copy的field不是简单的属性，而是个对象的属性的情况，比如 area.id或者a.b.c.filed这种多层结构。预期结果是在target对象上建立path指定的层级关系，如果中间层为空则new个map。最终把field(最后一个.后面的部分)从source复制到target的对应field上
     * @param target
     * @param source
     * @param path
     * @return
     */
    def handleFieldsPath(Map target, Object source, Object field){

        def path = isMap(field) ? field.name : field
        String sourceField, targetField

        (sourceField, targetField) = path.split('->').size()==1 ? [path, path] : path.split('->')

        Object targetMiddleObj, sourceMiddleObj
        String  targetFieldName, sourceFieldName
        (targetMiddleObj, targetFieldName) = loopPath(target, targetField, true)
        (sourceMiddleObj, sourceFieldName) = loopPath(source, sourceField, false)

        if(isMap(field) && field.ignore?.call(sourceMiddleObj)){  //忽略往target下copy字段
            return
        }

        targetMiddleObj[targetFieldName] = sourceMiddleObj ?
            ((isMap(field) && field.formatter) ? field.formatter.call(sourceMiddleObj[sourceFieldName]): sourceMiddleObj[sourceFieldName]) : ""
    }

    static boolean isMap(Object field){
        field instanceof Map
    }


    def loopPath(Object obj, String path, boolean createOnNull){

        def pathSegments = path.split('\\.')
        def middleObj = obj
        pathSegments.eachWithIndex{ def pathSegment, int i ->
            if(i!=(pathSegments.size()-1)){
                if(createOnNull && !middleObj[pathSegment]) {
                    middleObj[pathSegment] = new HashMap()
                }
                middleObj = middleObj ? middleObj[pathSegment] : null
            }
        }
        [middleObj, pathSegments.last()]
    }

}
