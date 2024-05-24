[#ftl]
[@b.head/]
[@b.grid items=supervisors var="supervisor"]
  [@b.gridbar]
    bar.addItem("${b.text("action.new")}",action.add());
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("${b.text("action.delete")}",action.remove("确认删除?"));
    bar.addItem("导入",action.method('importForm'));
    bar.addItem("${b.text("action.export")}",action.exportData("user.code:职工号,user.name:姓名,level.name:督导类型,beginOn:生效日期,endOn:失效日期",null,'fileName=听课人信息'));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="10%" property="user.code" title="职工号"/]
    [@b.col width="10%" property="user.name" title="姓名"/]
    [@b.col property="user.department.name" title="部门"/]
    [@b.col width="15%" property="level.name" title="督导类型"/]
    [@b.col width="15%" property="beginOn" title="生效日期"]${supervisor.beginOn!}~${supervisor.endOn!}[/@]
    [@b.col width="20%" property="remark" title="备注"/]
  [/@]
[/@]
[@b.foot/]
