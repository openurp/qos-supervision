[#ftl]
[@b.head/]

[@b.nav class="nav-tabs nav-tabs-compact"]
  [@b.navitem href="clazz"]听课列表[/@]
  [@b.navitem href="category"]听课分类[/@]
[/@]

<div class="search-container">
    <div class="search-panel">
    [@b.form name="searchForm" action="!search" target="supervisionList" title="ui.searchForm" theme="search"]
      [@base.semester name="supervisionClazz.semester.id" value=semester label="学年学期" /]
      [@b.textfield name="supervisionClazz.clazz.crn" label="课程序号" maxlength="4000"/]
      [@b.textfield name="supervisionClazz.clazz.course.code" label="课程代码"/]
      [@b.textfield name="supervisionClazz.clazz.course.name" label="课程名称"/]
      [@b.select name="category.id" label="课程分类" items=categories empty="..." /]
      [@b.select name="supervisionClazz.clazz.teachDepart.id" label="开课院系" items=departs empty="..."/]
    [/@]
    </div>
    <div class="search-list">
      [@b.div id="supervisionList" href="!search?supervisionClazz.semester.id=${semester.id}&orderBy=supervisionClazz.clazz.crn"/]
    </div>
  </div>
[@b.foot/]
