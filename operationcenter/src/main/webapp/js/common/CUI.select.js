/**
 * @Param
 * store:map类型数据集
 * maxHeight:下拉框最大高度，数据超出是显示滚动条
 * input:展示选择项text的input元素
 * hidden:可选,展示选择项id的hidden元素
 * all:可选，是否显示“全部”
 * @type {{store: string, maxHeight: number, input: string, hidden: string, all: boolean, show: Function, hide: Function}}
 */
CUI.select = {
    store: "",
    maxHeight: 0,
    input: "",
    hidden: "",
    all: true,
    selected:[],
    show: function (input, height, map, all, hidden) {
        this.input = input;
        this.maxHeight = height;
        this.store = map;
        this.all = all;
        this.hidden = hidden;
        buildList();
    },
    showTag: function (input, height, map, all, hidden, tag) {
        this.input = input;
        this.maxHeight = height;
        this.store = map;
        this.all = all;
        this.hidden = hidden;
        this.tag = tag;
        buildLabelList();

    },
    hide: function () {
        if (this.input && this.input.next() != null) {
            this.input.next().remove();
        }
    }
}

function buildList() {
    showBuildList();
    CUI.select.input.next().find("li").unbind("click").bind("click", function () {
        SetData($(this).find("a").html(), $(this).find("a").attr("ItemData"));
    });
    function SetData(Text, Data) {
        CUI.select.input.val(Text);
        if (CUI.select.hidden) {
            CUI.select.hidden.val(Data);
        }
        CUI.select.hide();

        // if (o.onSelect) { o.onSelect(o.InputName,Data); }
    }

}

function showBuildList() {
    CUI.select.hide();
    var width = CUI.select.input.width();
    var bool = false;
    var left = CUI.select.input.position().left;
    var top = CUI.select.input.position().top + 35;
    var html = "<div id='dropDownList'><ul class='dropdown-menu dropdown-menu-down' role='menu' style='left:auto;top:" + top + "px;'>";
    if (CUI.select.all) {
        html = html + "<li><a ItemData='0' >" + '全部' + "</a></li><li class='divider'></li>";
    }
    CUI.select.store.each(function (key, value, index) {
        bool = true;
        if (index > 0) {
            html = html + "<li class='divider'></li>";
        }
        html = html + "<li><a ItemData='" + key + "' >" + value + "</a></li>";
    })
    html = html + "</ul></div>";

    if (bool) {
        CUI.select.input.after(html);
        CUI.select.input.next().find("ul").width(width);
        CUI.select.input.next().find("ul").css("left", left + "px");
        CUI.select.input.next().find("ul").show();
    }
    if (CUI.select.maxHeight > 0) {
        var UL = CUI.select.input.next().find("ul");
        if (UL.height() > CUI.select.maxHeight) {
            UL.css({'height': CUI.select.maxHeight, 'overflow': 'auto'});
        }
    }
}
function buildLabelList() {
    showBuildList();
    CUI.select.input.next().find("li").unbind("click").bind("click", function () {
        SetData($(this).find("a").html(), $(this).find("a").attr("ItemData"));
        $(".tagator_tag_remove").unbind("click").bind({
            click:function () {
                CUI.select.selected.remove($(this).next(".areaId").val());
                $(this).parent().remove();
            }
        })
    });
    function SetData(Text, Data) {


        var new_tag = $(".tagator_tags").html();
        if(CUI.select.selected.indexOf(Data) == -1 ){
            new_tag = new_tag +
                "<div class='tagator_tag'>" + Text +
                "<div class='tagator_tag_remove'>×</div><input type='hidden' class='areaId' name='area' value='" + Data +
                "'><div style='clear: both;'></div></div>";
            CUI.select.selected.push(Data);
        }
        $(".tagator_tags").html(new_tag);
        $("#result_detail").val('');
        CUI.select.hide();
        CUI.select.input.val('');

    }
}
