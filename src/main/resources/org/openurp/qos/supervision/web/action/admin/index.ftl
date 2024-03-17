[#ftl]
[@b.head/]
[@b.toolbar title="听课管理"/]
<div class="search-container">
    <div class="search-panel">
    [@b.form name="searchForm" action="!search" target="supervisionList" title="ui.searchForm" theme="search"]
      [@base.semester name="supervision.clazz.semester.id" value=semester label="学年学期" /]
      [@b.textfield name="supervision.clazz.crn" label="课程序号" maxlength="4000"/]
      [@b.textfield name="supervision.clazz.course.code" label="课程代码"/]
      [@b.textfield name="supervision.clazz.course.name" label="课程名称"/]
      [@b.select name="supervision.level.id" label="听课类型" items=levels empty="..." /]
      [@b.select name="supervision.clazz.teachDepart.id" label="开课院系" items=departs empty="..."/]
      [@b.date name="supervision.assessOn" label="听课日期"/]
      [@b.textfield name="supervision.assessor.name" label="听课人"/]

      <input type="hidden" name="orderBy" value="supervision.assessOn desc"/>
    [/@]
    </div>
    <div class="search-list">
      [@b.div id="supervisionList" href="!search?supervision.clazz.semester.id=${semester.id}&orderBy=supervision.assessOn desc"/]
    </div>
  </div>
[@b.foot/]
