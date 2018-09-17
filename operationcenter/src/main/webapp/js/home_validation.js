(function($){
    var isValidatePattern=null;
    // 检测
    var Validation=function(){
        var rules={
            required:{
                check:function(value){
                    if(value){
                        return true;
                    }else{
                        return false;
                    }
                },
                msg:'此项是必须要填写的'
            },
            email:{
                check:function(value){
                    if(value)
                        return isValidatePattern(value,/^\w+@[a-z\d]+\.(com|cn|com.cn|net|org)$/);
                    return false;
                },
                msg:'请输入有效的邮件地址'
            },
            password:{
                check:function(value){
                    if(value.length<6 || value.length>12){
                        return false;
                    }else{
                        return isValidatePattern(value,/^\w{6,12}$/);
                    }
                },
                msg:'由6到12位字母数字下划线组成'
            }
        }
        // 检测函数
        isValidatePattern=function(value,pattern){
            var regex=pattern;
            return regex.test(value);
        }
        // 返回一个对象，这个对象提供了获取和添加的接口
        return {
            addRule:function(name,obj){
                rules[name]=obj;
            },
            getRule:function(name){
                return rules[name];
            }
        }
    }
    // 表单
    var Form=function(form){
        var fields=[];
        form.find('[validation]').each(function(){
            var field=$(this);  // input
            if(field.attr('validation')!==undefined){
                fields.push(new Field(field));  // 添加每一个filed的实例
            }
        })
        this.fields=fields;
    }

    Form.prototype={
        // 提交表单时检测所有文本框
        validForm:function(){
            for(var field in this.fields){
                this.fields[field].validate();
            }
        },
        // 判断表单是否具备了提交的条件
        isValit:function(){
            for(var field in this.fields){
                if(!this.fields[field].valit){
                    this.fields[field].f.focus();
                    return false;
                }
            }
            return true;
        }
    }

    // Field每一个要检测的表单元素的构造函数
    var Field=function(field){   // field就是每一个要检测的input
        this.f=field;
        this.valit=false;
        this.attach("focus");
        this.attach("change");
    }

    Field.prototype={
        attach:function(event){
            var obj=this;
            if(event=='focus'){
                this.f.bind('focus',function(){
                    if($(this).val()==''){
                        var $tip=$(this).next('p');
                        $('[node-type][class=""]').hide();
                        $tip.show().text($tip.attr('node-type'));
                    }
                })
            }

            if(event=='change'){
                this.f.bind('change',function(){
                    obj.validate();
                })
            }
            if(event=='keyup'){
                this.f.bind('keyup',function(){
                    obj.validate();
                })
            }
        },
        validate:function(){
            var obj=this,
                field=this.f,
                container=field.parent(),
                p=field.next('p'),
                type=field.attr('validation'),
                rules=$.validation.getRule(type),
                msg=p.attr('node-type');
            field.unbind('keyup');
            obj.attach('keyup');
            p.show();
            if(!rules.check(field.val())){
                container.addClass('errorbox');
                field.next('p').text(msg).addClass('error');
                obj.valit=false;
            }else{
                field.next('p').empty().removeClass().addClass('right');
                container.removeClass('errorbox');
                obj.valit=true;
            }

        }
    }


    $.extend($.fn,{
        validation:function(){
            $.validation=new Validation();
            // 通过调用addRule方法向rules这个对象里添加一个检测手机号码的对象
            // 创建一个表单实例
            var validator=new Form($(this));
            $(this).bind('submit',function(e){
                // 检测每一个文本框
                validator.validForm();
                if(!validator.isValit()){
                    // 阻止事件的默认行为
                    e = e || window.event;
                    if(e.preventDefault){
                        e.preventDefault();
                    }else{
                        e.returnValue=false;
                    }
                }

            })
        }
    })
})(jQuery)