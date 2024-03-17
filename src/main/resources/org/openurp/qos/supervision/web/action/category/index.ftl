[#ftl]
[@b.head/]

[@b.nav class="nav-tabs nav-tabs-compact"]
  [@b.navitem href="clazz"]听课列表[/@]
  [@b.navitem href="category"]听课分类[/@]
[/@]

<div class="search-container">
    <div class="search-panel">
    [@b.form name="supervisionClazzCategorySearchForm" action="!search" target="supervisionClazzCategorylist" title="ui.searchForm" theme="search"]
      [@b.textfields names="supervisionClazzCategory.code;代码"/]
      [@b.textfields names="supervisionClazzCategory.name;名称"/]
      [@b.textfields names="supervisionClazzCategory.enName;英文名称"/]
      [@b.select label="是否有效"  name="active" items={"1":"是","0":"否"} value="1" empty="..."/]
      <input type="hidden" name="orderBy" value="supervisionClazzCategory.code"/>
    [/@]
    </div>
    <div class="search-list">
      [@b.div id="supervisionClazzCategorylist" href="!search?orderBy=supervisionClazzCategory.code&active=1"/]
    </div>
  </div>
[@b.foot/]
