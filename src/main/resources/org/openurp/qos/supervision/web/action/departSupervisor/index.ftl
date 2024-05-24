[@b.head/]
[@b.toolbar title="听课名单管理"/]
<div class="search-container">
  <div class="search-panel">
    [@b.form name="supervisorSearchForm" action="!search" title="ui.searchForm" target="supervisorList" theme="search"]
      [@b.textfield name="supervisor.user.code" label="工号" maxlength="5000"/]
      [@b.textfield name="supervisor.user.name" label="姓名"/]
      [@b.select items=departs name="supervisor.user.department.id" label="所在部门"/]
      [@b.select items=levels name="supervisor.level.id" label="督导类型"/]
      [@b.textfield name="supervisor.remark" label="备注"/]
      <input type="hidden" name="orderBy" value="supervisor.beginOn desc"/>
    [/@]
  </div>
  <div class="search-list">
    [@b.div id="supervisorList" href="!search?orderBy=supervisor.beginOn desc"/]
  </div>
</div>
[@b.foot/]
