// controllers/dashboardController.js
const User = require('../models/userModel');
const Employee = require('../models/employeeModel');
const Department = require('../models/departmentModel');
const Task = require('../models/taskModel');
const Evaluation = require('../models/evaluationModel');
const Attendance = require('../models/attendanceModel');

// controllers/dashboardController.js
exports.getDepManagerDashboard = async (req, res) => {
    try {
        const userId = req.user.id;
        const user = await User.findById(userId);

        if (!user || user.role !== 'dep_manager') {
            return res.status(403).json({
                success: false,
                message: 'Access denied. Only department managers can view this dashboard.'
            });
        }

        // Lấy thông tin employee từ user_id
        const employee = await Employee.findByUserId(userId);
        if (!employee) {
            return res.status(404).json({ success: false, message: 'Employee info not found.' });
        }

        // Lấy thông tin phòng ban của manager
        let department = null;
        if (employee.department_id) {
            department = await Department.findById(employee.department_id);
        }

        // Lấy tổng số nhân viên thuộc phòng ban này (không lọc status)
        let totalCount = 0;
        let activeCount = 0;
        if (department) {
            // Đếm tổng số nhân viên PB
            const [allRows] = await require('../config/db').execute(
                `SELECT COUNT(*) as total FROM employees WHERE department_id = ?`,
                [department.id]
            );
            totalCount = allRows[0]?.total || 0;

            // Đếm nhân viên đang active
            const [activeRows] = await require('../config/db').execute(
                `SELECT COUNT(*) as active FROM employees WHERE department_id = ? AND status = 'active'`,
                [department.id]
            );
            activeCount = activeRows[0]?.active || 0;
        }

        // Trả dữ liệu về cho frontend
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
                employee_count_active: activeCount,
                employee_count_total: totalCount,
                // ...phần khác giữ nguyên nếu cần
            }
        });
    } catch (error) {
        console.error('[Dashboard] Error:', error);
        return res.status(500).json({ success: false, message: 'Server error', error: error.message });
    }
};