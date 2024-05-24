[@b.head/]
[#assign hasSemester= Parameters['supervision.clazz.semester.id']?? && Parameters['supervision.clazz.semester.id']?length>0/]
  [@b.grid items=supervisions var="supervision"]
    [@b.gridbar]
      bar.addItem("填写",action.method("inputSearch",null,null,"_blank"));
      bar.addItem("查看",action.single("info",null,null,"_blank"));
      bar.addItem("修改",action.single("edit",null,null,"_blank"));
      bar.addItem("${b.text("action.delete")}",action.remove("确认删除?"));
      [#if hasSemester]
      bar.addItem("导出",action.method("exportData",null,null,"_blank"));
      [/#if]
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="序号" width="4%" property="clazz.crn"/]
      [@b.col title="课程名称" property="clazz.course.name"/]
      [@b.col title="教学班" width="13%"]
        <div style="max-width: 200px;" class="text-ellipsis" title="${supervision.clazz.clazzName}">${supervision.clazz.clazzName}</div>
      [/@]
      [@b.col title="任课教师" width="7%"]
        [#list supervision.clazz.teachers as t]${t.name}[#sep],[/#list]
      [/@]
      [@b.col title="开课院系" property="clazz.teachDepart.name" width="6%"]
        ${supervision.clazz.teachDepart.shortName!supervision.clazz.teachDepart.name}
      [/@]
      [@b.col title="听课人" property="assessor.name" width="8%"/]
      [@b.col title="听课日期" property="assessOn" width="8%"]
        [@b.a href="!info?id="+supervision.id target="_blank"]${supervision.assessOn?string('yyyy-MM-dd')}[/@]
      [/@]
      [@b.col title="地点" property="room" width="10%"/]
      [@b.col title="得分" property="score" width="5%"/]
      [#if !hasSemester]
      [@b.col title="学年学期" property="clazz.semester.id" width="8%"]${supervision.clazz.semester.schoolYear}-${supervision.clazz.semester.name}[/@]
      [/#if]
    [/@]
  [/@]
 [#list 1..5 as i]<br>[/#list]
[@b.foot/]
