(function() {
    // API 基础路径
    var API_BASE = '';

    // 地址类型数据（后续可改为接口获取）
    var ADDRESS_TYPES = [
        { code: '01', name: '其他地址' },
        { code: '02', name: '联系地址' },
        { code: '03', name: '居住地址' },
        { code: '04', name: '单位地址' },
        { code: '05', name: '户籍地址' },
        { code: '06', name: '证件地址' },
        { code: '07', name: '营业地址' },
        { code: '08', name: '注册地址' },
        { code: '09', name: '办公地址' },
        { code: '10', name: '永久地址' }
    ];

    // Toast 消息弹窗
    function showToast(message, type, duration) {
        var existing = document.querySelector('.toast');
        if (existing) existing.remove();
        var toast = document.createElement('div');
        toast.className = 'toast ' + type;
        toast.textContent = message;
        document.body.appendChild(toast);
        setTimeout(function() {
            toast.remove();
        }, duration || 3000);
    }

    // 状态变量
    var state = {
        currentClientNo: '',
        currentPage: 1,
        pageSize: 5
    };

    // DOM 元素缓存
    var dom = {};

    // 初始化 DOM 元素缓存
    function initDom() {
        dom.clientNoInput = document.getElementById('clientNoInput');
        dom.queryBtn = document.getElementById('queryBtn');
        dom.addBtn = document.getElementById('addBtn');
        dom.addressTableBody = document.getElementById('addressTableBody');
        dom.pageInfo = document.getElementById('pageInfo');
        dom.prevBtn = document.getElementById('prevBtn');
        dom.nextBtn = document.getElementById('nextBtn');
        dom.addressModal = document.getElementById('addressModal');
        dom.modalTitle = document.getElementById('modalTitle');
        dom.addressForm = document.getElementById('addressForm');
        dom.seqNoInput = document.getElementById('seqNo');
        dom.addressTypeSelect = document.getElementById('addressType');
        dom.addressDetailInput = document.getElementById('addressDetail');
        dom.cancelBtn = document.getElementById('cancelBtn');
        dom.pageSizeSelect = document.getElementById('pageSizeSelect');
    }

    // 调用查询接口
    function queryAddresses(clientNo, pageNum, pageSize, addressType) {
        return fetch(API_BASE + '/client/address/query', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                clientNo: clientNo,
                pageNum: pageNum,
                pageSize: pageSize,
                addressType: addressType || null
            })
        }).then(function(response) {
            return response.json();
        }).then(function(result) {
            if (result.code !== '00000' && result.code !== '200') {
                throw new Error(result.message || '查询失败');
            }
            return result.data;
        });
    }

    // 调用单地址更新接口
    function updateSingleAddress(address) {
        return fetch(API_BASE + '/client/address/single/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(address)
        }).then(function(response) {
            return response.json();
        }).then(function(result) {
            if (result.code !== '00000' && result.code !== '200') {
                throw new Error(result.message || '操作失败');
            }
            return result.data;
        });
    }

    // 获取地址类型显示名称
    function getAddressTypeName(typeCode) {
        for (var i = 0; i < ADDRESS_TYPES.length; i++) {
            if (ADDRESS_TYPES[i].code === typeCode) {
                return ADDRESS_TYPES[i].name;
            }
        }
        return typeCode;
    }

    // 获取地址类型代码
    function getAddressTypeCode(typeName) {
        for (var i = 0; i < ADDRESS_TYPES.length; i++) {
            if (ADDRESS_TYPES[i].name === typeName) {
                return ADDRESS_TYPES[i].code;
            }
        }
        return typeName;
    }

    // 渲染地址类型下拉框
    function renderAddressTypeSelect() {
        var html = '';
        for (var i = 0; i < ADDRESS_TYPES.length; i++) {
            var t = ADDRESS_TYPES[i];
            html += '<option value="' + t.code + '">' + t.name + '</option>';
        }
        dom.addressTypeSelect.innerHTML = html;
    }

    // 刷新地址列表
    function refreshAddressList() {
        return queryAddresses(state.currentClientNo, state.currentPage, state.pageSize)
            .then(function(data) {
                renderAddressTable(data.list);
                renderPagination(data.total, state.currentPage, Math.ceil(data.total / state.pageSize));
            });
    }

    // 渲染地址列表
    function renderAddressTable(addresses) {
        dom.addressTableBody.innerHTML = '';
        if (!addresses || addresses.length === 0) {
            dom.addressTableBody.innerHTML = '<tr><td colspan="7" style="text-align:center;">暂无数据</td></tr>';
            return;
        }
        for (var i = 0; i < addresses.length; i++) {
            var addr = addresses[i];
            var tr = document.createElement('tr');
            tr.innerHTML =
                '<td>' + (addr.seqNo || '') + '</td>' +
                '<td>' + getAddressTypeName(addr.addressType) + '</td>' +
                '<td>' + (addr.addressDetail || '') + '</td>' +
                '<td>' + (addr.lastChangeDate || '') + '</td>' +
                '<td><span class="' + (addr.isMailingAddress === 'Y' ? 'flag-yes' : 'flag-no') + '">' + (addr.isMailingAddress === 'Y' ? '√' : '×') + '</span></td>' +
                '<td><span class="' + (addr.isNewest === 'Y' ? 'flag-yes' : 'flag-no') + '">' + (addr.isNewest === 'Y' ? '√' : '×') + '</span></td>' +
                '<td>' +
                '<button class="editBtn" data-seqno="' + addr.seqNo + '">编辑</button>' +
                '<button class="deleteBtn" data-seqno="' + addr.seqNo + '">删除</button>' +
                '</td>';
            dom.addressTableBody.appendChild(tr);
        }
    }

    // 渲染分页信息
    function renderPagination(total, currentPage, totalPages) {
        dom.pageInfo.textContent = '共 ' + total + ' 条  第 ' + currentPage + '/' + (totalPages || 1) + ' 页';
        dom.prevBtn.disabled = currentPage <= 1;
        dom.nextBtn.disabled = currentPage >= totalPages;
    }

    // 打开弹窗
    function openModal(data) {
        if (data) {
            dom.modalTitle.textContent = '编辑地址';
            dom.seqNoInput.value = data.seqNo || '';
            dom.addressTypeSelect.value = data.addressType || '03';
            dom.addressDetailInput.value = data.addressDetail || '';
            var mailingRadio = document.querySelector('input[name="isMailingAddress"][value="' + (data.isMailingAddress === 'Y' ? 'Y' : 'N') + '"]');
            if (mailingRadio) mailingRadio.checked = true;
            var newestRadio = document.querySelector('input[name="isNewest"][value="' + (data.isNewest === 'Y' ? 'Y' : 'N') + '"]');
            if (newestRadio) newestRadio.checked = true;
        } else {
            dom.modalTitle.textContent = '新增地址';
            dom.addressForm.reset();
            dom.seqNoInput.value = '';
        }
        dom.addressModal.style.display = 'flex';
    }

    // 关闭弹窗
    function closeModal() {
        dom.addressModal.style.display = 'none';
        dom.addressForm.reset();
    }

    // 绑定事件
    function bindEvents() {
        // 查询按钮
        dom.queryBtn.addEventListener('click', function() {
            var clientNo = dom.clientNoInput.value.trim();
            if (!clientNo) {
                showToast('请输入客户号', 'info');
                return;
            }
            state.currentClientNo = clientNo;
            state.currentPage = 1;
            queryAddresses(state.currentClientNo, state.currentPage, state.pageSize)
                .then(function(data) {
                    renderAddressTable(data.list);
                    renderPagination(data.total, state.currentPage, Math.ceil(data.total / state.pageSize));
                })
                .catch(function(e) {
                    showToast(e.message, 'error');
                });
        });

        // 新增按钮
        dom.addBtn.addEventListener('click', function() {
            openModal();
        });

        // 上一页
        dom.prevBtn.addEventListener('click', function() {
            if (state.currentPage <= 1) return;
            state.currentPage--;
            queryAddresses(state.currentClientNo, state.currentPage, state.pageSize)
                .then(function(data) {
                    renderAddressTable(data.list);
                    renderPagination(data.total, state.currentPage, Math.ceil(data.total / state.pageSize));
                })
                .catch(function(e) {
                    showToast(e.message, 'error');
                });
        });

        // 下一页
        dom.nextBtn.addEventListener('click', function() {
            state.currentPage++;
            queryAddresses(state.currentClientNo, state.currentPage, state.pageSize)
                .then(function(data) {
                    renderAddressTable(data.list);
                    renderPagination(data.total, state.currentPage, Math.ceil(data.total / state.pageSize));
                })
                .catch(function(e) {
                    showToast(e.message, 'error');
                });
        });

        // 表格操作按钮（编辑、删除）
        dom.addressTableBody.addEventListener('click', function(e) {
            var target = e.target;
            var seqNo = target.dataset.seqno;

            if (target.classList.contains('editBtn')) {
                var row = target.closest('tr');
                var cells = row.cells;
                openModal({
                    seqNo: cells[0].textContent,
                    addressType: getAddressTypeCode(cells[1].textContent),
                    addressDetail: cells[2].textContent,
                    isMailingAddress: cells[4].textContent.indexOf('√') >= 0 ? 'Y' : 'N',
                    isNewest: cells[5].textContent.indexOf('√') >= 0 ? 'Y' : 'N'
                });
            }

            if (target.classList.contains('deleteBtn')) {
                if (!confirm('确定删除该地址？')) return;
                updateSingleAddress({
                    seqNo: seqNo,
                    clientNo: state.currentClientNo,
                    delFlag: 'Y'
                })
                .then(function() {
                    showToast('删除成功', 'success');
                    return refreshAddressList();
                })
                .catch(function(e) {
                    showToast(e.message, 'error');
                });
            }
        });

        // 取消按钮
        dom.cancelBtn.addEventListener('click', closeModal);

        // 每页条数切换
        dom.pageSizeSelect.addEventListener('change', function() {
            state.pageSize = parseInt(dom.pageSizeSelect.value);
            state.currentPage = 1;
            queryAddresses(state.currentClientNo, state.currentPage, state.pageSize)
                .then(function(data) {
                    renderAddressTable(data.list);
                    renderPagination(data.total, state.currentPage, Math.ceil(data.total / state.pageSize));
                })
                .catch(function(e) {
                    showToast(e.message, 'error');
                });
        });

        // 表单提交
        dom.addressForm.addEventListener('submit', function(e) {
            e.preventDefault();
            var address = {
                seqNo: dom.seqNoInput.value || null,
                clientNo: state.currentClientNo,
                addressType: dom.addressTypeSelect.value,
                addressDetail: dom.addressDetailInput.value,
                isMailingAddress: document.querySelector('input[name="isMailingAddress"]:checked').value,
                isNewest: document.querySelector('input[name="isNewest"]:checked').value,
                delFlag: 'N'
            };
            updateSingleAddress(address)
                .then(function() {
                    showToast('保存成功', 'success');
                    closeModal();
                    return refreshAddressList();
                })
                .catch(function(e) {
                    showToast(e.message, 'error');
                });
        });
    }

    // 初始化
    function init() {
        initDom();
        renderAddressTypeSelect();
        bindEvents();
    }

    // 页面加载完成后初始化
    document.addEventListener('DOMContentLoaded', init);
})();
