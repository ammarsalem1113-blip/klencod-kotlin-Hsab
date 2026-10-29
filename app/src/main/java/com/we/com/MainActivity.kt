<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>إدارة بيانات الموظفين</title>
    <!-- Tailwind CSS CDN -->
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <!-- FontAwesome for Icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Cairo:wght@400;600;700&display=swap');
        body { font-family: 'Cairo', sans-serif; }
    </style>
</head>
<body class="bg-gray-50 text-gray-800 min-h-screen pb-24">

    <!-- Header -->
    <header class="bg-blue-600 text-white p-4 shadow-md sticky top-0 z-10">
        <div class="flex justify-between items-center max-w-md mx-auto">
            <h1 class="text-xl font-bold"><i class="fa-solid fa-users ml-2"></i> إدارة الموظفين</h1>
            <span id="employeeCount" class="bg-blue-700 text-xs px-2.5 py-1 rounded-full font-semibold">0 موظف</span>
        </div>
    </header>

    <main class="max-w-md mx-auto p-4 space-y-4">
        
        <!-- Form Card -->
        <div class="bg-white rounded-2xl p-4 shadow-sm border border-gray-100">
            <h2 id="formTitle" class="text-base font-bold text-gray-700 mb-3"><i class="fa-solid fa-user-plus text-blue-600 ml-1"></i> إضافة موظف جديد</h2>
            <form id="employeeForm" class="space-y-3">
                <input type="hidden" id="empId">
                <div>
                    <label class="block text-xs font-semibold text-gray-600 mb-1">اسم الموظف</label>
                    <input type="text" id="empName" required placeholder="أدخل اسم الموظف الكامل" 
                        class="w-full px-3 py-2 text-sm bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500">
                </div>
                <div>
                    <label class="block text-xs font-semibold text-gray-600 mb-1">رقم الهاتف</label>
                    <input type="tel" id="empPhone" required placeholder="07xxxxxxxxx" 
                        class="w-full px-3 py-2 text-sm bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500" dir="ltr" style="text-align: right;">
                </div>
                <div class="flex gap-2 pt-1">
                    <button type="submit" id="saveBtn" class="flex-1 bg-blue-600 hover:bg-blue-700 active:scale-95 text-white font-semibold py-2.5 px-4 rounded-xl text-sm transition shadow-sm">
                        حفظ البيانات
                    </button>
                    <button type="button" id="cancelBtn" onclick="resetForm()" class="hidden bg-gray-200 hover:bg-gray-300 text-gray-700 font-semibold py-2.5 px-4 rounded-xl text-sm transition">
                        إلغاء
                    </button>
                </div>
            </form>
        </div>

        <!-- Search Box -->
        <div class="relative">
            <span class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none text-gray-400">
                <i class="fa-solid fa-search"></i>
            </span>
            <input type="text" id="searchInput" oninput="renderEmployees()" placeholder="ابحث بالاسم أو رقم الهاتف..." 
                class="w-full pr-10 pl-4 py-2.5 text-sm bg-white border border-gray-200 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
        </div>

        <!-- Employees List -->
        <div class="space-y-2.5" id="employeeList">
            <!-- Dynamic Items will load here -->
        </div>

    </main>

    <!-- Toast Notification -->
    <div id="toast" class="fixed bottom-5 left-1/2 -translate-x-1/2 bg-gray-900 text-white px-4 py-2 rounded-xl text-xs shadow-lg opacity-0 pointer-events-none transition-opacity duration-300 z-50">
        تم الحفظ بنجاح
    </div>

    <!-- JavaScript Logic -->
    <script>
        let employees = JSON.parse(localStorage.getItem('employees')) || [];
        let isEditing = false;

        const form = document.getElementById('employeeForm');
        const empIdInput = document.getElementById('empId');
        const empNameInput = document.getElementById('empName');
        const empPhoneInput = document.getElementById('empPhone');
        const saveBtn = document.getElementById('saveBtn');
        const cancelBtn = document.getElementById('cancelBtn');
        const formTitle = document.getElementById('formTitle');
        const employeeList = document.getElementById('employeeList');
        const employeeCount = document.getElementById('employeeCount');

        form.addEventListener('submit', function(e) {
            e.preventDefault();
            const name = empNameInput.value.trim();
            const phone = empPhoneInput.value.trim();
            const id = empIdInput.value;

            if (isEditing) {
                // Update
                employees = employees.map(emp => emp.id == id ? { id: Number(id), name, phone } : emp);
                showToast('تم تعديل بيانات الموظف بنجاح');
            } else {
                // Add
                const newEmp = {
                    id: Date.now(),
                    name,
                    phone
                };
                employees.push(newEmp);
                showToast('تم إضافة الموظف بنجاح');
            }

            saveAndReset();
        });

        function renderEmployees() {
            const search = document.getElementById('searchInput').value.toLowerCase();
            const filtered = employees.filter(emp => emp.name.toLowerCase().includes(search) || emp.phone.includes(search));

            employeeCount.textContent = `${employees.length} موظف`;

            if (filtered.length === 0) {
                employeeList.innerHTML = `
                    <div class="text-center py-10 bg-white rounded-2xl border border-gray-100 shadow-sm">
                        <i class="fa-solid fa-folder-open text-3xl text-gray-300 mb-2"></i>
                        <p class="text-xs text-gray-400">لا توجد بيانات موظفين مسجلة</p>
                    </div>
                `;
                return;
            }

            employeeList.innerHTML = filtered.map(emp => `
                <div class="bg-white p-3.5 rounded-2xl border border-gray-100 shadow-sm flex items-center justify-between">
                    <div class="flex items-center gap-3">
                        <div class="w-10 h-10 bg-blue-50 text-blue-600 rounded-xl flex items-center justify-center font-bold text-sm">
                            ${emp.name.charAt(0)}
                        </div>
                        <div>
                            <h3 class="font-bold text-sm text-gray-800">${emp.name}</h3>
                            <a href="tel:${emp.phone}" class="text-xs text-blue-600 hover:underline flex items-center gap-1 mt-0.5" dir="ltr">
                                <i class="fa-solid fa-phone text-[10px]"></i> ${emp.phone}
                            </a>
                        </div>
                    </div>
                    <div class="flex items-center gap-1">
                        <button onclick="editEmployee(${emp.id})" class="w-8 h-8 rounded-lg bg-gray-50 text-gray-600 hover:bg-blue-50 hover:text-blue-600 flex items-center justify-center transition">
                            <i class="fa-solid fa-pen text-xs"></i>
                        </button>
                        <button onclick="deleteEmployee(${emp.id})" class="w-8 h-8 rounded-lg bg-gray-50 text-red-500 hover:bg-red-50 hover:text-red-600 flex items-center justify-center transition">
                            <i class="fa-solid fa-trash text-xs"></i>
                        </button>
                    </div>
                </div>
            `).join('');
        }

        function editEmployee(id) {
            const emp = employees.find(e => e.id == id);
            if (!emp) return;

            empIdInput.value = emp.id;