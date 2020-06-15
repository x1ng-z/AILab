
function newTab(url) {
    addTab(url);
}

let myelement1;
function setElem(data) {
    myelement1=data;
    console.log("add elemnt111111:");
    // console.log("add elemnt:",myelement1);
}

function addTab(name,url) {
    //新增选项卡



    console.log(url);
    $('#nav',parent.document)
    myelement1.tabAdd('pagetabs', {
        title: elem[0].innerText
        ,content: '<iframe src="'+url+'" class="layui-admin-iframe" scrolling="auto" frameborder="0"></iframe>'
        ,id: url
    });
    //切换选项卡
    myelement1.tabChange('pagetabs', url);
}