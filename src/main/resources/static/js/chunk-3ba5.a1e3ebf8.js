(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-3ba5"],{"00cc":function(t,e,o){},ab11:function(t,e,o){"use strict";var n=o("00cc"),i=o.n(n);i.a},b3d1:function(t,e,o){"use strict";o.r(e);var n=function(){var t=this,e=t.$createElement,o=t._self._c||e;return o("Card",[o("p",{attrs:{slot:"title"},slot:"title"},[o("Icon",{attrs:{type:"ios-list-box-outline"}}),t._v("\n        查看任务详情\n    ")],1),o("div",[o("h3",[t._v(t._s(t.job.jobName))]),o("br"),1===t.job.jobType?o("div",[o("code",[t._v("\n            请求地址: "+t._s(t.job.url)+" "),o("br"),t._v("\n            请求内容: "+t._s(t.job.body)+"\n            ")])]):t._e(),0===t.job.jobType?o("div",[t._v("\n            执行脚本: "+t._s(t.job.script)+"\n        ")]):t._e()]),o("Divider"),o("div",[o("h3",[t._v("执行日志")]),t._l(t.job.jobRecords,function(e){return o("div",{key:e.id,staticClass:"record"},[o("code",[t._v("执行时间: "+t._s(e.runTime))]),o("br"),o("code",[t._v("执行输出: "+t._s(e.result))])])})],2)],1)},i=[],s=(o("cadf"),o("551c"),o("097d"),{name:"TaskView",data:function(){return{job:{}}},created:function(){var t=this,e=this.$route.query.job;this.$axios.get("/jobView/"+e).then(function(e){t.job=e.data.data})}}),c=s,r=(o("ab11"),o("2877")),a=Object(r["a"])(c,n,i,!1,null,"410ccf11",null);a.options.__file="TaskView.vue";e["default"]=a.exports}}]);
//# sourceMappingURL=chunk-3ba5.a1e3ebf8.js.map