[#ftl]
[@b.head/]
[@b.grid items=supervisionClazzCategories var="supervisionClazzCategory"]
  [@b.gridbar]
    bar.addItem("${b.text("action.new")}",action.add());
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("${b.text("action.delete")}",action.remove("确认删除?"));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="15%" property="code" title="代码"]${supervisionClazzCategory.code}[/@]
    [@b.col property="name" title="名称"][@b.a href="!info?id=${supervisionClazzCategory.id}"]${supervisionClazzCategory.name}[/@][/@]
    [@b.col width="20%" property="beginOn" title="生效时间"]${supervisionClazzCategory.beginOn!}[/@]
    [@b.col width="20%" property="endOn" title="失效时间"]${supervisionClazzCategory.endOn!}[/@]
  [/@]
[/@]
[@b.foot/]
