[@b.head/]
  [@b.grid items=supervisionClazzes var="supervisionClazz"]
    [@b.gridbar]
      bar.addItem("添加课程",action.method("addSetting"));
      [#if (Parameters['category.id']!'')?length>0]bar.addItem("移出分类",action.multi("removeCategory"));[/#if]
      bar.addItem("${b.text("action.export")}",action.exportData("clazz.crn:课程序号,clazz.course.name:课程名称,clazz.courseType.name:课程类别,clazz.teachDepart.name:开课院系,clazz.clazzName:教学班,clazz.enrollment.stdCount:人数",null,'fileName=听课课程信息'));
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="课程序号" width="5%" property="clazz.crn"/]
      [@b.col title="任课教师" width="7%"]
        [#list supervisionClazz.clazz.teachers as t]${t.name}[#sep],[/#list]
      [/@]
      [@b.col title="课程名称" property="clazz.course.name"/]
      [@b.col title="课程类别" property="clazz.courseType.name" width="7%"]
        <div style="max-width: 100px;" class="text-ellipsis" title="${supervisionClazz.clazz.courseType.name}">${supervisionClazz.clazz.courseType.name}</div>
      [/@]
      [@b.col title="开课院系" property="clazz.teachDepart.name" width="7%"]
        ${supervisionClazz.clazz.teachDepart.shortName!supervisionClazz.clazz.teachDepart.name}
      [/@]
      [@b.col title="学分" property="clazz.course.defaultCredits" width="5%"/]
      [@b.col title="教学班" width="13%"]
        <div style="max-width: 200px;" class="text-ellipsis" title="${supervisionClazz.clazz.clazzName}">${supervisionClazz.clazz.clazzName}</div>
      [/@]
      [@b.col title="人数" property="clazz.enrollment.stdCount" width="5%"/]
      [@b.col title="上课时间" width="10%"]
        ${activities[supervisionClazz.clazz.id?string]!}
      [/@]
      [@b.col title="上课地点" width="10%"]
        ${rooms[supervisionClazz.clazz.id?string]!}
      [/@]
      [@b.col title="分类" width="13%"]
        [#list supervisionClazz.categories as c]${c.name}[#sep]<br>[/#list]
      [/@]
    [/@]
  [/@]
 [#list 1..5 as i]<br>[/#list]
[@b.foot/]
