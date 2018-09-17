quoteView:{
        init=function(){
            quoteView.$preferentialBtnGroup = $("#preferentialBtnGroup");
        }
        scope:{
            order:{
                btns:{
                    preferentialBtnGroup:{
                        show=function(){
                            manualQuoteObj.result_manual.supportManual(quote.companyId,function(support){
                                if(!support){
                                    quoteView.$preferentialBtnGroup.hide();
                                }
                            });
                        }
                    }
                    preferentialBtn:{
                        show=function(){

                        };
                        hide=function(){

                        }
                    }
                }
            }
        }
    }

