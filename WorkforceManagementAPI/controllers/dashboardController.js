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
        } else {
            // Nếu manager chưa gán phòng ban, trả về rỗng
            department = null;
        }

        // Thống kê số lượng nhân viên thuộc phòng ban này
        let deptEmployees = [];
        let deptEmployeeCount = 0;
        if (department) {
            deptEmployees = await Employee.getAll({
                page: 1,
                limit: 1000,
                search: '',
                filter: '',
            });
            deptEmployees = deptEmployees.employees.filter(e => e.department_id === department.id);
            deptEmployeeCount = deptEmployees.length;
        }

        // Thống kê số lượng công việc đang thực hiện trong phòng ban
        let deptTasks = [];
        let deptTasksInProgress = 0;
        let deptTasksCompletedThisWeek = 0;
        if (department) {
            deptTasks = await Task.getAll({
                page: 1,
                limit: 1000,
                assignee_id: undefined,
                search: '',
            });
            // Chỉ lấy task của nhân viên phòng ban
            deptTasks = deptTasks.tasks.filter(task => {
                return deptEmployees.some(emp => emp.id === task.assignee_id);
            });
            deptTasksInProgress = deptTasks.filter(task => ['pending', 'in_progress', 'review'].includes(task.status)).length;

            // Số task hoàn thành tuần này
            const startOfWeek = new Date();
            startOfWeek.setDate(startOfWeek.getDate() - startOfWeek.getDay());
            deptTasksCompletedThisWeek = deptTasks.filter(task =>
                task.status === 'completed' &&
                new Date(task.updated_at) >= startOfWeek
            ).length;
        }

        // Hiệu suất phòng ban: trung bình overall_score của các evaluation đã finalized
        let deptPerformance = 0;
        if (department) {
            const [rows] = await require('../config/db').execute(
                `SELECT AVG(ev.overall_score) AS avg_score
                 FROM evaluations ev
                 JOIN employees emp ON ev.employee_id = emp.id
                 WHERE emp.department_id = ? AND ev.status = 'finalized'`,
                [department.id]
            );
            deptPerformance = rows[0] && rows[0].avg_score ? Math.round(rows[0].avg_score) : 0;
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
                employee_total: deptEmployeeCount, // Nếu có tổng, sửa lại
                dept_performance: deptPerformance,
                tasks_in_progress: deptTasksInProgress,
                tasks_completed_this_week: deptTasksCompletedThisWeek
            }
        });
    } catch (error) {
        console.error('[Dashboard] Error:', error);
        return res.status(500).json({ success: false, message: 'Server error', error: error.message });
    }
};