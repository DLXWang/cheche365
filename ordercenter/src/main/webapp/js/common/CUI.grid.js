/**
 * Created by xu.yelong on 2015/12/12.
 */
CUI.grid={
        columns:"",
        store:"",
        search:"",
        html:"",
        dom:"",
        addIndex:false,
        row:{
           id:"",
           index:"",
           store:""
        }
        ,results:{
            success:"SUCCESS",
            failure:"FAILURE",
            notFound:"NOT_FOUND"
        }
}

CUI.grid.build=function(rowNum){
    var row=new tr();
    var content=createPrefix(row);
    if(CUI.grid.addIndex){
        var serialNumTd=new td();
        content+=createPrefix(serialNumTd)+createContent(rowNum+1)+createSuffix(serialNumTd);
    }
    $.each(CUI.grid.columns,function(i,column){
        var index=column.dataIndex;
        var cell=new td(null,true);
        if(!common.isEmpty(index)){
            cell=new td(eval('CUI.grid.row.store.'+index),true,index+"_id_"+rowNum,index,null);
        }
        if(column.renderer){
            var customResult=column.renderer(cell.content,rowNum,CUI.grid.row.store);
            if(!common.isEmpty(customResult)){
                cell.content=customResult;
            }
        }
        content+=createPrefix(cell)+createContent(cell.content,cell.required)+createSuffix(cell);
    })
    content+=createSuffix(new tr());
    CUI.grid.html+=content;
}

CUI.grid.addTR=function(tr,callBackMethod){
    CUI.grid.dom.append(tr);
    if(callBackMethod){
        callBackMethod();
    }
}

CUI.grid.removeTR=function(rowIndex,callBackMethod){
    var tr="#"+ROW.ID+rowIndex;
    CUI.grid.dom.find(tr).remove();
    if(callBackMethod){
        callBackMethod();
    }
}


CUI.grid.fill=function(){

    if(!CUI.grid.check()){

        return;
    }
    CUI.grid.html="";
    $.each(CUI.grid.store.viewList,function(i,rowStore){
        CUI.grid.row.store=rowStore;
        CUI.grid.row.id=ROW.ID+i;
        CUI.grid.row.index=i;
        CUI.grid.build(i);
    });
    CUI.grid.dom.html(CUI.grid.html);
}

CUI.grid.page=function(callBackMethod){
    if(CUI.grid.store==null){
        return;
    }
    $("#totalCount").text(CUI.grid.store.pageInfo.totalElements);
    if (CUI.grid.store.pageInfo.totalPage > 1) {
        $(".customer-pagination").show();
        $.jqPaginator('.pagination',
            {
                totalPages: CUI.grid.store.pageInfo.totalPage,
                visiblePages: CUI.grid.properties.visiblePages,
                currentPage: CUI.grid.properties.currentPage,
                onPageChange: function (pageNum, pageType) {
                    if (pageType == "change") {
                        CUI.grid.properties.currentPage = pageNum;
                        if(callBackMethod){
                            callBackMethod(true);
                        }
                    }
                }
            }
        );
    } else {
        $(".customer-pagination").hide();
    }
}

CUI.grid.check=function(){
    if (CUI.grid.store == null) {
        if(CUI.grid.result){
            CUI.grid.result.callback(CUI.grid.results.failure);
        }
        common.showTips("查询错误");
        return false;
    }else if (CUI.grid.store.pageInfo!=null&&CUI.grid.store.pageInfo.totalElements < 1) {
        if(CUI.grid.result){
            CUI.grid.result.callback(CUI.grid.results.notFound);
        }
        CUI.grid.dom.empty();
        common.showTips("未检索到符合条件的数据");
        return false;
    }else{
        if(CUI.grid.result){
            CUI.grid.result.callback(CUI.grid.results.success);
        }
        return true;
    }

}

var PROPERTY={
    ID:"id=",
    NAME:"name=",
    CLASS:"class=",
    STYLE:"style="
}

var TAG={
    PREFIX:"<",
    SUFFIX:">",
    SPACE:" ",
    END:"/"
};

var ROW={
    ATTR:"tr",
    ID:"tab_tr"
}


function tr(clazz,style,name){
    this.attr=ROW.ATTR;
    this.id=CUI.grid.row.id;
    this.name=name;
    this.style=style;
    this.class=clazz;
    this.tds=new Array();
}



function td(content,required,id,name,clazz){
    this.attr="td";
    this.id=id;
    this.name=name;
    this.class=clazz;
    this.style="text-align:center";
    this.content=content;
    this.required=required;
}

tr.prototype.put=function(td){
   this.tds.push(td);
}

tr.prototype.getHtml=function(){
    var content=createPrefix(this);
    $.each(this.tds,function(i,td){
        content+=createPrefix(td)+createContent(td.content,td.required)+createSuffix(td);
    })
    content+=createSuffix(this);
    return content;
}

function createPrefix(obj){
    var prefix=TAG.PREFIX+obj.attr;
    if(!common.isEmpty(obj.id)){
        prefix+=TAG.SPACE+PROPERTY.ID+"'"+obj.id+"'"+TAG.SPACE;
    }
    if(!common.isEmpty(obj.name)){
        prefix+=TAG.SPACE+PROPERTY.NAME+"'"+obj.name+"'"+TAG.SPACE;
    }
    if(!common.isEmpty(obj.class)){
        prefix+=TAG.SPACE+PROPERTY.CLASS+obj.class+TAG.SPACE;
    }
    if(!common.isEmpty(obj.style)){
        prefix+=TAG.SPACE+PROPERTY.STYLE+obj.style+TAG.SPACE;
    }
    prefix+=TAG.SUFFIX;
    return  prefix;
}

function createContent(content,required){
    if(required==true){
        return common.checkToEmpty(content);
    }
    return content;
}

function createSuffix(obj){
    return TAG.PREFIX+TAG.END+obj.attr+TAG.SUFFIX;
}


