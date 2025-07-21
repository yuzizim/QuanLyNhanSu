// controllers/dashboardController.js
const User = require('../models/userModel');
const Employee = require('../models/employeeModel');
const Department = require('../models/departmentModel');
const Task = require('../models/taskModel');
const Evaluation = require('../models/evaluationModel');
const Attendance = require('../models/attendanceModel');

exports.getDepManagerDashboard = async (req, res) => {
    try {
        // Lấy thông tin user đăng nhập
        const userId = req.user.id;
        const user = await User.findById(userId);

        console.log('[dashboard] userId:', userId, ', user:', user);

        if (!user || user.role !== 'dep_manager') {
            return res.status(403).json({
                success: false,
                message: 'Access denied. Only department managers can view this dashboard.'
            });
        }

        // Lấy thông tin employee từ user_id
        const employee = await Employee.findByUserId(userId);
        console.log('[dashboard] employee:', employee);

        if (!employee) {
            return res.status(404).json({ success: false, message: 'Employee info not found.' });
        }

        // Lấy thông tin phòng ban của manager
        let department = null;
        if (employee.department_id) {
            department = await Department.findById(employee.department_id);
        } else {
            department = null;
        }
        console.log('[dashboard] department:', department);

        // Thống kê số lượng nhân viên thuộc phòng ban này
        let deptEmployees = [];
        let deptEmployeeCount = 0;
        if (department) {
            let allEmployeesData = await Employee.getAll({
                page: 1,
                limit: 1000,
                search: ''
                // Không truyền filter nếu không dùng!
            });
            console.log('[dashboard] allEmployeesData:', allEmployeesData);

            // Ép kiểu để so sánh chắc chắn đúng (nếu department_id là null thì bỏ qua)
            deptEmployees = allEmployeesData.employees.filter(e =>
                e.department_id !== null &&
                String(e.department_id) === String(department.id)
            );
            deptEmployeeCount = deptEmployees.length;

            console.log('Type of e.department_id:', typeof deptEmployees[0]?.department_id, 'Value:', deptEmployees[0]?.department_id);
            console.log('Type of department.id:', typeof department.id, 'Value:', department.id);
        }

        // Chuẩn bị dữ liệu trả về
        return res.json({
            success: true,
            user: {
                id: user.id,
                username: user.username,
                email: user.email,
                role: user.role,
                full_name: `${employee.first_name} ${employee.last_name}`,
                department: department
                    ? { id: department.id, name: department.name, code: department.code }
                    : null
            },
            stats: {
                employee_count: deptEmployeeCount,
                employee_total: deptEmployeeCount // Nếu có tổng, sửa lại
                // ...
            }
        });
    } catch (error) {
        console.error('[Dashboard] Error:', error);
        return res.status(500).json({ success: false, message: 'Server error', error: error.message });
    }
};