<%--
  Created by IntelliJ IDEA.
  User: Steven
  Date: 2026/5/28
  Time: 19:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>图书馆管理系统 - 测试页面</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Microsoft YaHei', Arial, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }

        .container {
            max-width: 1400px;
            margin: 0 auto;
            background: white;
            border-radius: 10px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
            overflow: hidden;
        }

        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }

        .header h1 {
            font-size: 32px;
            margin-bottom: 10px;
        }

        .user-info {
            margin-top: 10px;
            font-size: 14px;
        }

        .nav-tabs {
            display: flex;
            background: #f8f9fa;
            border-bottom: 2px solid #dee2e6;
        }

        .nav-tab {
            flex: 1;
            padding: 15px 20px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s;
            border: none;
            background: transparent;
            font-size: 16px;
            font-weight: 500;
            color: #6c757d;
        }

        .nav-tab:hover {
            background: #e9ecef;
            color: #495057;
        }

        .nav-tab.active {
            background: white;
            color: #667eea;
            border-bottom: 3px solid #667eea;
        }

        .content {
            padding: 30px;
        }

        .tab-content {
            display: none;
        }

        .tab-content.active {
            display: block;
        }

        .section-title {
            font-size: 24px;
            color: #333;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #667eea;
        }

        .form-group {
            margin-bottom: 15px;
        }

        .form-group label {
            display: block;
            margin-bottom: 5px;
            color: #555;
            font-weight: 500;
        }

        .form-group input,
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 10px 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
            transition: border-color 0.3s;
        }

        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #667eea;
        }

        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.3s;
            margin-right: 10px;
            margin-bottom: 10px;
        }

        .btn-primary {
            background: #667eea;
            color: white;
        }

        .btn-primary:hover {
            background: #5568d3;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }

        .btn-success {
            background: #28a745;
            color: white;
        }

        .btn-success:hover {
            background: #218838;
        }

        .btn-danger {
            background: #dc3545;
            color: white;
        }

        .btn-danger:hover {
            background: #c82333;
        }

        .btn-warning {
            background: #ffc107;
            color: #333;
        }

        .btn-warning:hover {
            background: #e0a800;
        }

        .btn-info {
            background: #17a2b8;
            color: white;
        }

        .btn-info:hover {
            background: #138496;
        }

        .search-box {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
        }

        .search-box input {
            flex: 1;
            padding: 10px 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            background: white;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }

        table thead {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        table th,
        table td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #dee2e6;
        }

        table tbody tr:hover {
            background: #f8f9fa;
        }

        .message {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
            display: none;
        }

        .message.success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
            display: block;
        }

        .message.error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
            display: block;
        }

        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
        }

        .modal.show {
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .modal-content {
            background: white;
            padding: 30px;
            border-radius: 10px;
            width: 90%;
            max-width: 500px;
            max-height: 80vh;
            overflow-y: auto;
        }

        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .modal-header h3 {
            color: #333;
        }

        .close {
            font-size: 28px;
            font-weight: bold;
            color: #aaa;
            cursor: pointer;
            line-height: 20px;
        }

        .close:hover {
            color: #000;
        }

        .grid-2 {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 15px;
        }

        @media (max-width: 768px) {
            .grid-2 {
                grid-template-columns: 1fr;
            }

            .nav-tabs {
                flex-wrap: wrap;
            }

            .nav-tab {
                flex: 1 1 50%;
            }
        }

        .badge {
            padding: 5px 10px;
            border-radius: 3px;
            font-size: 12px;
            font-weight: 500;
        }

        .badge-success {
            background: #28a745;
            color: white;
        }

        .badge-danger {
            background: #dc3545;
            color: white;
        }

        .badge-warning {
            background: #ffc107;
            color: #333;
        }

        .badge-info {
            background: #17a2b8;
            color: white;
        }

        .action-btns {
            display: flex;
            gap: 5px;
            flex-wrap: wrap;
        }

        .action-btns .btn {
            padding: 5px 10px;
            font-size: 12px;
            margin: 0;
        }

        .stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .stat-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 10px;
            text-align: center;
        }

        .stat-card h3 {
            font-size: 32px;
            margin-bottom: 5px;
        }

        .stat-card p {
            font-size: 14px;
            opacity: 0.9;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📚 图书馆管理系统</h1>
            <div class="user-info" id="userInfo">
                <span>未登录</span>
                <button class="btn btn-primary" onclick="showLoginModal()" style="margin-left: 10px;">登录</button>
                <button class="btn btn-success" onclick="showRegisterModal()">注册</button>
            </div>
        </div>

        <div class="nav-tabs">
            <button class="nav-tab active" onclick="switchTab('books')">📖 图书管理</button>
            <button class="nav-tab" onclick="switchTab('cart')">🛒 购物车</button>
            <button class="nav-tab" onclick="switchTab('borrow')">📋 借阅管理</button>
            <button class="nav-tab" onclick="switchTab('users')">👥 用户管理</button>
            <button class="nav-tab" onclick="switchTab('categories')">🏷️ 分类管理</button>
        </div>

        <div class="content">
            <div id="messageBox" class="message"></div>

            <!-- 图书管理 -->
            <div id="books" class="tab-content active">
                <h2 class="section-title">图书列表</h2>
                <div class="search-box">
                    <input type="text" id="searchKeyword" placeholder="搜索图书名称、作者或ISBN...">
                    <button class="btn btn-primary" onclick="searchBooks()">搜索</button>
                    <button class="btn btn-success" onclick="showAddBookModal()">添加图书</button>
                    <button class="btn btn-info" onclick="loadBooks()">刷新</button>
                </div>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>书名</th>
                            <th>作者</th>
                            <th>ISBN</th>
                            <th>出版社</th>
                            <th>价格</th>
                            <th>库存</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody id="bookTableBody">
                        <tr>
                            <td colspan="8" style="text-align: center;">加载中...</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- 购物车 -->
            <div id="cart" class="tab-content">
                <h2 class="section-title">我的购物车</h2>
                <div class="stats" id="cartStats" style="display: none;">
                    <div class="stat-card">
                        <h3 id="cartTotalAmount">¥0.00</h3>
                        <p>购物车总金额</p>
                    </div>
                    <div class="stat-card">
                        <h3 id="cartItemCount">0</h3>
                        <p>商品数量</p>
                    </div>
                </div>
                <button class="btn btn-info" onclick="loadCart()" style="margin-bottom: 20px;">刷新</button>
                <button class="btn btn-danger" onclick="clearCart()" style="margin-bottom: 20px;">清空购物车</button>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>书名</th>
                            <th>作者</th>
                            <th>单价</th>
                            <th>数量</th>
                            <th>小计</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody id="cartTableBody">
                        <tr>
                            <td colspan="7" style="text-align: center;">请先登录查看</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- 借阅管理 -->
            <div id="borrow" class="tab-content">
                <h2 class="section-title">我的借阅记录</h2>
                <button class="btn btn-info" onclick="loadMyBorrows()" style="margin-bottom: 20px;">刷新</button>
                <table>
                    <thead>
                        <tr>
                            <th>记录ID</th>
                            <th>图书ID</th>
                            <th>借阅时间</th>
                            <th>归还时间</th>
                            <th>状态</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody id="myBorrowTableBody">
                        <tr>
                            <td colspan="6" style="text-align: center;">请先登录查看</td>
                        </tr>
                    </tbody>
                </table>

                <h2 class="section-title" style="margin-top: 40px;">所有借阅记录</h2>
                <button class="btn btn-info" onclick="loadAllBorrows()" style="margin-bottom: 20px;">刷新</button>
                <table>
                    <thead>
                        <tr>
                            <th>记录ID</th>
                            <th>用户ID</th>
                            <th>图书ID</th>
                            <th>借阅时间</th>
                            <th>归还时间</th>
                            <th>状态</th>
                        </tr>
                    </thead>
                    <tbody id="allBorrowTableBody">
                        <tr>
                            <td colspan="6" style="text-align: center;">加载中...</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- 用户管理 -->
            <div id="users" class="tab-content">
                <h2 class="section-title">用户列表</h2>
                <button class="btn btn-info" onclick="loadUsers()" style="margin-bottom: 20px;">刷新</button>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>用户名</th>
                            <th>角色</th>
                            <th>状态</th>
                            <th>注册时间</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody id="userTableBody">
                        <tr>
                            <td colspan="6" style="text-align: center;">加载中...</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- 分类管理 -->
            <div id="categories" class="tab-content">
                <h2 class="section-title">图书分类</h2>
                <button class="btn btn-success" onclick="showAddCategoryModal()" style="margin-bottom: 20px;">添加分类</button>
                <button class="btn btn-info" onclick="loadCategories()">刷新</button>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>分类名称</th>
                            <th>描述</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody id="categoryTableBody">
                        <tr>
                            <td colspan="4" style="text-align: center;">加载中...</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- 登录模态框 -->
    <div id="loginModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>用户登录</h3>
                <span class="close" onclick="closeModal('loginModal')">&times;</span>
            </div>
            <form onsubmit="login(event)">
                <div class="form-group">
                    <label>用户名</label>
                    <input type="text" id="loginUsername" required>
                </div>
                <div class="form-group">
                    <label>密码</label>
                    <input type="password" id="loginPassword" required>
                </div>
                <button type="submit" class="btn btn-primary">登录</button>
            </form>
        </div>
    </div>

    <!-- 注册模态框 -->
    <div id="registerModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>用户注册</h3>
                <span class="close" onclick="closeModal('registerModal')">&times;</span>
            </div>
            <form onsubmit="register(event)">
                <div class="form-group">
                    <label>用户名</label>
                    <input type="text" id="registerUsername" required>
                </div>
                <div class="form-group">
                    <label>密码</label>
                    <input type="password" id="registerPassword" required>
                </div>
                <button type="submit" class="btn btn-success">注册</button>
            </form>
        </div>
    </div>

    <!-- 添加/编辑图书模态框 -->
    <div id="bookModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3 id="bookModalTitle">添加图书</h3>
                <span class="close" onclick="closeModal('bookModal')">&times;</span>
            </div>
            <form onsubmit="saveBook(event)">
                <input type="hidden" id="bookId">
                <div class="grid-2">
                    <div class="form-group">
                        <label>书名</label>
                        <input type="text" id="bookName" required>
                    </div>
                    <div class="form-group">
                        <label>作者</label>
                        <input type="text" id="author">
                    </div>
                </div>
                <div class="grid-2">
                    <div class="form-group">
                        <label>ISBN</label>
                        <input type="text" id="isbn">
                    </div>
                    <div class="form-group">
                        <label>出版社</label>
                        <input type="text" id="publisher">
                    </div>
                </div>
                <div class="grid-2">
                    <div class="form-group">
                        <label>价格</label>
                        <input type="number" id="price" step="0.01" min="0">
                    </div>
                    <div class="form-group">
                        <label>库存</label>
                        <input type="number" id="stock" min="0">
                    </div>
                </div>
                <button type="submit" class="btn btn-primary">保存</button>
            </form>
        </div>
    </div>

    <!-- 添加/编辑分类模态框 -->
    <div id="categoryModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3 id="categoryModalTitle">添加分类</h3>
                <span class="close" onclick="closeModal('categoryModal')">&times;</span>
            </div>
            <form onsubmit="saveCategory(event)">
                <input type="hidden" id="categoryId">
                <div class="form-group">
                    <label>分类名称</label>
                    <input type="text" id="categoryName" required>
                </div>
                <div class="form-group">
                    <label>描述</label>
                    <textarea id="categoryDesc" rows="3"></textarea>
                </div>
                <button type="submit" class="btn btn-primary">保存</button>
            </form>
        </div>
    </div>

    <script>
        let currentUser = null;
        
        // 获取应用上下文路径
        const contextPath = '${pageContext.request.contextPath}';

        // 页面加载时获取图书列表
        window.onload = function() {
            loadBooks();
        };

        // 切换标签页
        function switchTab(tabName) {
            document.querySelectorAll('.tab-content').forEach(tab => {
                tab.classList.remove('active');
            });
            document.querySelectorAll('.nav-tab').forEach(tab => {
                tab.classList.remove('active');
            });

            document.getElementById(tabName).classList.add('active');
            event.target.classList.add('active');

            if (tabName === 'books') {
                loadBooks();
            } else if (tabName === 'cart') {
                loadCart();
            } else if (tabName === 'borrow') {
                loadMyBorrows();
                loadAllBorrows();
            } else if (tabName === 'users') {
                loadUsers();
            } else if (tabName === 'categories') {
                loadCategories();
            }
        }

        // 显示消息
        function showMessage(message, type) {
            const messageBox = document.getElementById('messageBox');
            messageBox.textContent = message;
            messageBox.className = 'message ' + type;
            setTimeout(() => {
                messageBox.className = 'message';
            }, 3000);
        }

        // 模态框操作
        function showLoginModal() {
            document.getElementById('loginModal').classList.add('show');
        }

        function showRegisterModal() {
            document.getElementById('registerModal').classList.add('show');
        }

        function showAddBookModal() {
            document.getElementById('bookModalTitle').textContent = '添加图书';
            document.getElementById('bookId').value = '';
            document.getElementById('bookName').value = '';
            document.getElementById('author').value = '';
            document.getElementById('isbn').value = '';
            document.getElementById('publisher').value = '';
            document.getElementById('price').value = '';
            document.getElementById('stock').value = '';
            document.getElementById('bookModal').classList.add('show');
        }

        function showEditBookModal(book) {
            document.getElementById('bookModalTitle').textContent = '编辑图书';
            document.getElementById('bookId').value = book.id;
            document.getElementById('bookName').value = book.bookName;
            document.getElementById('author').value = book.author;
            document.getElementById('isbn').value = book.isbn;
            document.getElementById('publisher').value = book.publisher;
            document.getElementById('price').value = book.price;
            document.getElementById('stock').value = book.stock;
            document.getElementById('bookModal').classList.add('show');
        }

        function showAddCategoryModal() {
            document.getElementById('categoryModalTitle').textContent = '添加分类';
            document.getElementById('categoryId').value = '';
            document.getElementById('categoryName').value = '';
            document.getElementById('categoryDesc').value = '';
            document.getElementById('categoryModal').classList.add('show');
        }

        function showEditCategoryModal(category) {
            document.getElementById('categoryModalTitle').textContent = '编辑分类';
            document.getElementById('categoryId').value = category.categoryId;
            document.getElementById('categoryName').value = category.categoryName;
            document.getElementById('categoryDesc').value = category.categoryDesc;
            document.getElementById('categoryModal').classList.add('show');
        }

        function closeModal(modalId) {
            document.getElementById(modalId).classList.remove('show');
        }

        // 点击模态框外部关闭
        window.onclick = function(event) {
            if (event.target.classList.contains('modal')) {
                event.target.classList.remove('show');
            }
        };

        // API 请求封装
        async function apiRequest(url, method = 'GET', data = null) {
            const options = {
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                }
            };

            if (data && (method === 'POST' || method === 'PUT')) {
                options.body = JSON.stringify(data);
            }
            
            // 确保URL以/开头，避免路径问题
            if (!url.startsWith('/')) {
                url = '/' + url;
            }

            // 如果调用方已经传入了包含上下文路径的URL，则不要再次拼接，避免重复的上下文路径
            const fullUrl = url.startsWith(contextPath) ? url : contextPath + url;

            try {
                const response = await fetch(fullUrl, options);
                
                // 检查HTTP状态码
                if (!response.ok) {
                    console.error('HTTP错误:', response.status, response.statusText, 'URL:', fullUrl);
                    showMessage(`请求失败: ${response.status} ${response.statusText}`, 'error');
                    return null;
                }
                
                // 检查响应内容类型
                const contentType = response.headers.get('content-type');
                if (!contentType || !contentType.includes('application/json')) {
                    console.error('响应不是JSON格式:', contentType);
                    const text = await response.text();
                    console.error('响应内容:', text.substring(0, 500));
                    showMessage('服务器返回格式错误', 'error');
                    return null;
                }
                
                const result = await response.json();
                return result;
            } catch (error) {
                console.error('API请求失败:', error);
                showMessage(`网络请求失败: ${error.message}`, 'error');
                return null;
            }
        }

        // 用户登录
        async function login(event) {
            event.preventDefault();
            const username = document.getElementById('loginUsername').value;
            const password = document.getElementById('loginPassword').value;

            const result = await apiRequest(contextPath + '/api/users?action=login', 'POST', {
                username: username,
                password: password
            });

            if (result && result.code === 200) {
                currentUser = result.data;
                updateUserUI();
                closeModal('loginModal');
                showMessage('登录成功！', 'success');
                document.getElementById('loginUsername').value = '';
                document.getElementById('loginPassword').value = '';
            } else {
                showMessage(result.message || '登录失败', 'error');
            }
        }

        // 用户注册
        async function register(event) {
            event.preventDefault();
            const username = document.getElementById('registerUsername').value;
            const password = document.getElementById('registerPassword').value;

            const result = await apiRequest(contextPath + '/api/users?action=register', 'POST', {
                username: username,
                password: password
            });

            if (result && result.code === 200) {
                showMessage('注册成功！请登录', 'success');
                closeModal('registerModal');
                document.getElementById('registerUsername').value = '';
                document.getElementById('registerPassword').value = '';
            } else {
                showMessage(result.message || '注册失败', 'error');
            }
        }

        // 更新用户界面
        function updateUserUI() {
            const userInfo = document.getElementById('userInfo');
            if (currentUser) {
                userInfo.innerHTML = `
                    <span>欢迎，\${currentUser.username} (\${currentUser.role === 'admin' ? '管理员' : '普通用户'})</span>
                    <button class="btn btn-danger" onclick="logout()" style="margin-left: 10px;">退出登录</button>
                `;
            } else {
                userInfo.innerHTML = `
                    <span>未登录</span>
                    <button class="btn btn-primary" onclick="showLoginModal()" style="margin-left: 10px;">登录</button>
                    <button class="btn btn-success" onclick="showRegisterModal()">注册</button>
                `;
            }
        }

        // 退出登录
        function logout() {
            currentUser = null;
            updateUserUI();
            showMessage('已退出登录', 'success');
        }

        // 加载图书列表
        async function loadBooks() {
            const result = await apiRequest(contextPath + '/api/books');
            if (result && result.code === 200) {
                renderBookTable(result.data);
            } else {
                showMessage('加载图书失败', 'error');
            }
        }

        // 搜索图书
        async function searchBooks() {
            const keyword = document.getElementById('searchKeyword').value.trim();
            if (!keyword) {
                loadBooks();
                return;
            }

            const result = await apiRequest(contextPath + '/api/books?keyword=' + encodeURIComponent(keyword));
            if (result && result.code === 200) {
                renderBookTable(result.data);
            } else {
                showMessage('搜索失败', 'error');
            }
        }

        // 渲染图书表格
        function renderBookTable(books) {
            const tbody = document.getElementById('bookTableBody');
            if (!books || books.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" style="text-align: center;">暂无数据</td></tr>';
                return;
            }

            tbody.innerHTML = books.map(book => `
                <tr>
                    <td>\${book.id}</td>
                    <td>\${book.bookName}</td>
                    <td>\${book.author || '-'}</td>
                    <td>\${book.isbn || '-'}</td>
                    <td>\${book.publisher || '-'}</td>
                    <td>¥\${book.price ? book.price.toFixed(2) : '0.00'}</td>
                    <td><span class="badge \${book.stock > 0 ? 'badge-success' : 'badge-danger'}">\${book.stock}</span></td>
                    <td class="action-btns">
                        <button class="btn btn-success" onclick='addToCart(\${JSON.stringify(book)})'>加入购物车</button>
                        <button class="btn btn-primary" onclick='borrowBook(\${book.id})'>借阅</button>
                        <button class="btn btn-warning" onclick='showEditBookModal(\${JSON.stringify(book)})'>编辑</button>
                        <button class="btn btn-danger" onclick="deleteBook(\${book.id})">删除</button>
                    </td>
                </tr>
            `).join('');
        }

        // 保存图书
        async function saveBook(event) {
            event.preventDefault();
            const bookId = document.getElementById('bookId').value;
            const bookData = {
                bookName: document.getElementById('bookName').value,
                author: document.getElementById('author').value,
                isbn: document.getElementById('isbn').value,
                publisher: document.getElementById('publisher').value,
                price: parseFloat(document.getElementById('price').value) || 0,
                stock: parseInt(document.getElementById('stock').value) || 0
            };

            let result;
            if (bookId) {
                bookData.id = parseInt(bookId);
                result = await apiRequest(contextPath + '/api/books', 'PUT', bookData);
            } else {
                result = await apiRequest(contextPath + '/api/books', 'POST', bookData);
            }

            if (result && result.code === 200) {
                showMessage(bookId ? '更新成功' : '添加成功', 'success');
                closeModal('bookModal');
                loadBooks();
            } else {
                showMessage(result.message || '操作失败', 'error');
            }
        }

        // 删除图书
        async function deleteBook(id) {
            if (!confirm('确定要删除这本图书吗？')) {
                return;
            }

            const result = await apiRequest(contextPath + '/api/books/' + id, 'DELETE');
            if (result && result.code === 200) {
                showMessage('删除成功', 'success');
                loadBooks();
            } else {
                showMessage(result.message || '删除失败', 'error');
            }
        }

        // 借阅图书
        async function borrowBook(bookId) {
            if (!currentUser) {
                showMessage('请先登录', 'error');
                showLoginModal();
                return;
            }

            if (!confirm('确定要借阅这本图书吗？')) {
                return;
            }

            const result = await apiRequest(contextPath + '/api/borrows', 'POST', {
                action: 'borrow',
                bookId: bookId
            });

            if (result && result.code === 200) {
                showMessage('借阅成功', 'success');
                loadBooks();
            } else {
                showMessage(result.message || '借阅失败', 'error');
            }
        }

        // 加载我的借阅记录
        async function loadMyBorrows() {
            if (!currentUser) {
                document.getElementById('myBorrowTableBody').innerHTML =
                    '<tr><td colspan="6" style="text-align: center;">请先登录查看</td></tr>';
                return;
            }

            const result = await apiRequest(contextPath + '/api/borrows/my');
            if (result && result.code === 200) {
                renderMyBorrowTable(result.data);
            } else {
                showMessage('加载借阅记录失败', 'error');
            }
        }

        // 渲染我的借阅表格
        function renderMyBorrowTable(records) {
            const tbody = document.getElementById('myBorrowTableBody');
            if (!records || records.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" style="text-align: center;">暂无借阅记录</td></tr>';
                return;
            }

            tbody.innerHTML = records.map(record => `
                <tr>
                    <td>\${record.recordId}</td>
                    <td>\${record.bookId}</td>
                    <td>\${formatDateTime(record.borrowTime)}</td>
                    <td>\${record.returnTime ? formatDateTime(record.returnTime) : '-'}</td>
                    <td>
                        <span class="badge \${record.borrowStatus === 0 ? 'badge-warning' : 'badge-success'}">
                            \${record.borrowStatus === 0 ? '借阅中' : '已归还'}
                        </span>
                    </td>
                    <td>
                        \${record.borrowStatus === 0 ?
                            `<button class="btn btn-success" onclick="returnBook(\${record.recordId})">归还</button>` :
                            '-'}
                    </td>
                </tr>
            `).join('');
        }

        // 加载所有借阅记录
        async function loadAllBorrows() {
            const result = await apiRequest(contextPath + '/api/borrows');
            if (result && result.code === 200) {
                renderAllBorrowTable(result.data);
            } else {
                showMessage('加载借阅记录失败', 'error');
            }
        }

        // 渲染所有借阅表格
        function renderAllBorrowTable(records) {
            const tbody = document.getElementById('allBorrowTableBody');
            if (!records || records.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" style="text-align: center;">暂无数据</td></tr>';
                return;
            }

            tbody.innerHTML = records.map(record => `
                <tr>
                    <td>\${record.recordId}</td>
                    <td>\${record.userId}</td>
                    <td>\${record.bookId}</td>
                    <td>\${formatDateTime(record.borrowTime)}</td>
                    <td>\${record.returnTime ? formatDateTime(record.returnTime) : '-'}</td>
                    <td>
                        <span class="badge \${record.borrowStatus === 0 ? 'badge-warning' : 'badge-success'}">
                            \${record.borrowStatus === 0 ? '借阅中' : '已归还'}
                        </span>
                    </td>
                </tr>
            `).join('');
        }

        // 归还图书
        async function returnBook(recordId) {
            if (!confirm('确定要归还这本图书吗？')) {
                return;
            }

            const result = await apiRequest(contextPath + '/api/borrows', 'POST', {
                action: 'return',
                recordId: recordId
            });

            if (result && result.code === 200) {
                showMessage('归还成功', 'success');
                loadMyBorrows();
                loadAllBorrows();
                loadBooks();
            } else {
                showMessage(result.message || '归还失败', 'error');
            }
        }

        // 加载用户列表
        async function loadUsers() {
            const result = await apiRequest(contextPath + '/api/users');
            if (result && result.code === 200) {
                renderUserTable([result.data]);
            } else if (result && result.code === 401) {
                document.getElementById('userTableBody').innerHTML =
                    '<tr><td colspan="6" style="text-align: center;">请先登录查看</td></tr>';
            } else {
                showMessage('加载用户列表失败', 'error');
            }
        }

        // 渲染用户表格
        function renderUserTable(users) {
            const tbody = document.getElementById('userTableBody');
            if (!users || users.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" style="text-align: center;">暂无数据</td></tr>';
                return;
            }

            tbody.innerHTML = users.map(user => `
                <tr>
                    <td>\${user.id}</td>
                    <td>\${user.username}</td>
                    <td><span class="badge \${user.role === 'admin' ? 'badge-info' : 'badge-success'}">\${user.role}</span></td>
                    <td><span class="badge \${user.status === 1 ? 'badge-success' : 'badge-danger'}">\${user.status === 1 ? '正常' : '禁用'}</span></td>
                    <td>\${formatDateTime(user.createTime)}</td>
                    <td class="action-btns">
                        <button class="btn btn-danger" onclick="deleteUser(\${user.id})">删除</button>
                    </td>
                </tr>
            `).join('');
        }

        // 删除用户
        async function deleteUser(id) {
            if (!confirm('确定要删除这个用户吗？')) {
                return;
            }

            const result = await apiRequest(contextPath + '/api/users/' + id, 'DELETE');
            if (result && result.code === 200) {
                showMessage('删除成功', 'success');
                loadUsers();
            } else {
                showMessage(result.message || '删除失败', 'error');
            }
        }

        // 加载分类列表
        async function loadCategories() {
            const result = await apiRequest(contextPath + '/api/categories');
            if (result && result.code === 200) {
                renderCategoryTable(result.data);
            } else {
                showMessage('加载分类失败', 'error');
            }
        }

        // 渲染分类表格
        function renderCategoryTable(categories) {
            const tbody = document.getElementById('categoryTableBody');
            if (!categories || categories.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4" style="text-align: center;">暂无数据</td></tr>';
                return;
            }

            tbody.innerHTML = categories.map(category => `
                <tr>
                    <td>\${category.categoryId}</td>
                    <td>\${category.categoryName}</td>
                    <td>\${category.categoryDesc || '-'}</td>
                    <td class="action-btns">
                        <button class="btn btn-warning" onclick='showEditCategoryModal(\${JSON.stringify(category)})'>编辑</button>
                        <button class="btn btn-danger" onclick="deleteCategory(\${category.categoryId})">删除</button>
                    </td>
                </tr>
            `).join('');
        }

        // 保存分类
        async function saveCategory(event) {
            event.preventDefault();
            const categoryId = document.getElementById('categoryId').value;
            const categoryData = {
                categoryName: document.getElementById('categoryName').value,
                categoryDesc: document.getElementById('categoryDesc').value
            };

            let result;
            if (categoryId) {
                categoryData.categoryId = parseInt(categoryId);
                result = await apiRequest(contextPath + '/api/categories', 'PUT', categoryData);
            } else {
                result = await apiRequest(contextPath + '/api/categories', 'POST', categoryData);
            }

            if (result && result.code === 200) {
                showMessage(categoryId ? '更新成功' : '添加成功', 'success');
                closeModal('categoryModal');
                loadCategories();
            } else {
                showMessage(result.message || '操作失败', 'error');
            }
        }

        // 删除分类
        async function deleteCategory(id) {
            if (!confirm('确定要删除这个分类吗？')) {
                return;
            }

            const result = await apiRequest(contextPath + '/api/categories/' + id, 'DELETE');
            if (result && result.code === 200) {
                showMessage('删除成功', 'success');
                loadCategories();
            } else {
                showMessage(result.message || '删除失败', 'error');
            }
        }

        // 格式化日期时间
        function formatDateTime(dateTimeStr) {
            if (!dateTimeStr) return '-';
            const date = new Date(dateTimeStr);
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');
            const seconds = String(date.getSeconds()).padStart(2, '0');
            return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
        }

        // 添加到购物车
        async function addToCart(book) {
            if (!currentUser) {
                showMessage('请先登录', 'error');
                showLoginModal();
                return;
            }

            const result = await apiRequest(contextPath + '/api/cart', 'POST', {
                action: 'add',
                bookId: book.id,
                bookName: book.bookName,
                author: book.author,
                price: book.price,
                quantity: 1
            });

            if (result && result.code === 200) {
                showMessage('已加入购物车', 'success');
            } else {
                showMessage(result.message || '添加失败', 'error');
            }
        }

        // 加载购物车
        async function loadCart() {
            if (!currentUser) {
                document.getElementById('cartTableBody').innerHTML =
                    '<tr><td colspan="7" style="text-align: center;">请先登录查看</td></tr>';
                document.getElementById('cartStats').style.display = 'none';
                return;
            }

            const result = await apiRequest(contextPath + '/api/cart/list');
            if (result && result.code === 200) {
                renderCartTable(result.data);
                loadCartTotal();
            } else {
                showMessage('加载购物车失败', 'error');
            }
        }

        // 渲染购物车表格
        function renderCartTable(cartItems) {
            const tbody = document.getElementById('cartTableBody');
            if (!cartItems || cartItems.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" style="text-align: center;">购物车为空</td></tr>';
                document.getElementById('cartItemCount').textContent = '0';
                return;
            }

            document.getElementById('cartItemCount').textContent = cartItems.length;

            tbody.innerHTML = cartItems.map(item => `
                <tr>
                    <td>${item.id}</td>
                    <td>${item.bookName}</td>
                    <td>${item.author || '-'}</td>
                    <td>¥${item.price ? item.price.toFixed(2) : '0.00'}</td>
                    <td>
                        <input type="number" value="${item.quantity}" min="1" 
                               onchange="updateCartQuantity(${item.id}, this.value)"
                               style="width: 80px; padding: 5px;">
                    </td>
                    <td>¥${item.totalPrice ? item.totalPrice.toFixed(2) : '0.00'}</td>
                    <td class="action-btns">
                        <button class="btn btn-danger" onclick="deleteCartItem(${item.id})">删除</button>
                    </td>
                </tr>
            `).join('');
        }

        // 加载购物车总金额
        async function loadCartTotal() {
            if (!currentUser) return;

            const result = await apiRequest(contextPath + '/api/cart/total');
            if (result && result.code === 200) {
                document.getElementById('cartTotalAmount').textContent = 
                    '¥' + result.data.totalAmount.toFixed(2);
                document.getElementById('cartStats').style.display = 'grid';
            }
        }

        // 更新购物车商品数量
        async function updateCartQuantity(cartItemId, quantity) {
            if (!currentUser) return;

            const result = await apiRequest(contextPath + '/api/cart', 'PUT', {
                cartItemId: cartItemId,
                quantity: parseInt(quantity)
            });

            if (result && result.code === 200) {
                showMessage('更新成功', 'success');
                loadCart();
            } else {
                showMessage(result.message || '更新失败', 'error');
            }
        }

        // 删除购物车商品
        async function deleteCartItem(cartItemId) {
            if (!confirm('确定要删除这个商品吗？')) {
                return;
            }

            const result = await apiRequest(contextPath + '/api/cart/' + cartItemId, 'DELETE');
            if (result && result.code === 200) {
                showMessage('删除成功', 'success');
                loadCart();
            } else {
                showMessage(result.message || '删除失败', 'error');
            }
        }

        // 清空购物车
        async function clearCart() {
            if (!confirm('确定要清空购物车吗？')) {
                return;
            }

            const result = await apiRequest(contextPath + '/api/cart', 'POST', {
                action: 'clear'
            });

            if (result && result.code === 200) {
                showMessage('清空成功', 'success');
                loadCart();
            } else {
                showMessage(result.message || '清空失败', 'error');
            }
        }
    </script>
</body>
</html>

