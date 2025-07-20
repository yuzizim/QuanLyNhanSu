// attendanceController.js

// const Attendance = require('../models/attendanceModel');
// const Employee = require('../models/employeeModel');
// const moment = require('moment');

// // Check if employee exists
// const checkEmployeeExistence = async (employeeId) => {
//     const employee = await Employee.findById(employeeId);
//     return !!employee;
// };

// // Calculate attendance status based on schedule
// const calculateStatus = (check_in_time, check_out_time, schedule) => {
//     if (!schedule) return 'present'; // Default if no schedule
//     const checkIn = moment(check_in_time);
//     const shiftStart = moment(`${schedule.date} ${schedule.shift_start}`);
//     const shiftEnd = moment(`${schedule.date} ${schedule.shift_end}`);
//     let status = 'present';

//     // Check for late (more than 15 minutes after shift start)
//     if (checkIn.isAfter(shiftStart.add(15, 'minutes'))) {
//         status = 'late';
//     }

//     // Check for early leave (more than 15 minutes before shift end)
//     if (check_out_time && moment(check_out_time).isBefore(shiftEnd.subtract(15, 'minutes'))) {
//         status = status === 'late' ? 'late' : 'early_leave';
//     }

//     return status;
// };

// // Create a new attendance record
// exports.createAttendance = async (req, res) => {
//     try {
//         const { employee_id, date, check_in_time, check_out_time, notes } = req.body;
//         const user = req.user; // From verifyToken middleware

//         // Validate employee existence
//         if (!(await checkEmployeeExistence(employee_id))) {
//             return res.status(400).json({ message: 'Employee not found' });
//         }

//         // Authorization: Only admin, hr, or the employee themselves
//         if (!['admin', 'hr'].includes(user.role) && user.employee_id !== employee_id) {
//             return res.status(403).json({ message: 'Unauthorized to record attendance for this employee' });
//         }

//         // Get schedule
//         const schedule = await Attendance.getSchedule(employee_id, date);

//         // Calculate status
//         const status = calculateStatus(check_in_time, check_out_time, schedule);

//         // Validate check-out time if provided
//         if (check_out_time && moment(check_out_time).isBefore(moment(check_in_time))) {
//             return res.status(400).json({ message: 'Check-out time must be after check-in time' });
//         }

//         // Create attendance record
//         const attendanceData = { employee_id, date, check_in_time, check_out_time, status, notes };
//         const newAttendance = await Attendance.create(attendanceData);

//         if (!newAttendance) {
//             return res.status(400).json({ message: 'Failed to create attendance record' });
//         }

//         // Create notifications for late or early leave
//         if (['late', 'early_leave'].includes(status)) {
//             const employee = await Employee.findById(employee_id);
//             const message = `Attendance on ${date}: ${status === 'late' ? 'Late arrival' : 'Early leave'}`;
//             await Attendance.createNotification(employee.user_id, 'Attendance Alert', message, 'attendance');

//             // Notify HR
//             const [hrUsers] = await db.execute(`SELECT user_id FROM users WHERE role = 'hr'`);
//             for (const hr of hrUsers) {
//                 await Attendance.createNotification(
//                     hr.user_id,
//                     'Employee Attendance Alert',
//                     `${employee.first_name} ${employee.last_name} was ${status} on ${date}`,
//                     'attendance'
//                 );
//             }
//         }

//         return res.status(201).json({ message: 'Attendance recorded successfully', attendance: newAttendance });
//     } catch (error) {
//         console.error('Error creating attendance:', error);
//         return res.status(500).json({ message: error.message || 'Internal server error' });
//     }
// };

// // Get attendance records
// exports.getAttendance = async (req, res) => {
//     try {
//         const { employee_id, start_date, end_date, status, department_id, page, limit } = req.query;
//         const user = req.user;

//         // Authorization
//         let filters = { page: parseInt(page) || 1, limit: parseInt(limit) || 10 };
//         if (employee_id) filters.employee_id = parseInt(employee_id);
//         if (start_date) filters.start_date = start_date;
//         if (end_date) filters.end_date = end_date;
//         if (status) filters.status = status;
//         if (department_id) filters.department_id = parseInt(department_id);

//         if (user.role === 'employee') {
//             // Employees can only view their own records
//             filters.employee_id = user.employee_id;
//         } else if (user.role === 'dep_manager') {
//             // Department managers can only view their department
//             const manager = await Employee.findById(user.employee_id);
//             filters.department_id = manager.department_id;
//         }

//         const result = await Attendance.getAll(filters);
//         return res.status(200).json({
//             message: 'Attendance records retrieved successfully',
//             ...result
//         });
//     } catch (error) {
//         console.error('Error fetching attendance:', error);
//         return res.status(500).json({ message: error.message || 'Internal server error' });
//     }
// };

// // Update an attendance record
// exports.updateAttendance = async (req, res) => {
//     try {
//         const { id } = req.params;
//         const { check_in_time, check_out_time, notes, status: providedStatus } = req.body;
//         const user = req.user;

//         // Authorization: Only admin and hr can update
//         if (!['admin', 'hr'].includes(user.role)) {
//             return res.status(403).json({ message: 'Unauthorized to update attendance' });
//         }

//         // Get existing attendance record
//         const attendance = await Attendance.findById(id);
//         if (!attendance) {
//             return res.status(404).json({ message: 'Attendance record not found' });
//         }

//         // Get schedule
//         const schedule = await Attendance.getSchedule(attendance.employee_id, attendance.date);

//         // Calculate status if not provided
//         const status = providedStatus || calculateStatus(
//             check_in_time || attendance.check_in_time,
//             check_out_time || attendance.check_out_time,
//             schedule
//         );

//         // Validate check-out time if provided
//         if (check_out_time && moment(check_out_time).isBefore(moment(check_in_time || attendance.check_in_time))) {
//             return res.status(400).json({ message: 'Check-out time must be after check-in time' });
//         }

//         // Update attendance
//         const updateData = { check_in_time, check_out_time, status, notes };
//         const isUpdated = await Attendance.update(id, updateData);

//         if (!isUpdated) {
//             return res.status(400).json({ message: 'No changes detected or update failed' });
//         }

//         // Create notifications for status changes
//         if (['late', 'early_leave'].includes(status) && status !== attendance.status) {
//             const employee = await Employee.findById(attendance.employee_id);
//             const message = `Attendance updated on ${attendance.date}: ${status === 'late' ? 'Late arrival' : 'Early leave'}`;
//             await Attendance.createNotification(employee.user_id, 'Attendance Update', message, 'attendance');

//             // Notify HR
//             const [hrUsers] = await db.execute(`SELECT user_id FROM users WHERE role = 'hr'`);
//             for (const hr of hrUsers) {
//                 await Attendance.createNotification(
//                     hr.user_id,
//                     'Employee Attendance Update',
//                     `${employee.first_name} ${employee.last_name} attendance updated to ${status} on ${attendance.date}`,
//                     'attendance'
//                 );
//             }
//         }

//         const updatedAttendance = await Attendance.findById(id);
//         return res.status(200).json({ message: 'Attendance updated successfully', attendance: updatedAttendance });
//     } catch (error) {
//         console.error('Error updating attendance:', error);
//         return res.status(500).json({ message: error.message || 'Internal server error' });
//     }
// };

const dotenv = require('dotenv');
const Attendance = require('../models/attendanceModel');
const Employee = require('../models/employeeModel');
const db = require('../config/db.js');
const moment = require('moment-timezone');

const checkEmployeeExistence = async (employeeId) => {
    const [rows] = await db.execute(
        'SELECT id, status, user_id, first_name, last_name, department_id FROM employees WHERE id = ?',
        [employeeId]
    );
    return rows[0] || null;
};

const calculateStatus = (check_in_time, check_out_time, schedule) => {
    if (!schedule) {
        console.log('No schedule found, defaulting to present');
        return 'present';
    }

    //console.log('Raw schedule data:', schedule);

    // Validate schedule fields
    if (!moment(schedule.date, 'YYYY-MM-DD', true).isValid()) {
        console.error('Invalid schedule date:', schedule.date);
        return 'present';
    }
    if (!moment(schedule.shift_start, 'HH:mm:ss', true).isValid()) {
        console.error('Invalid shift_start:', schedule.shift_start);
        return 'present';
    }
    if (!moment(schedule.shift_end, 'HH:mm:ss', true).isValid()) {
        console.error('Invalid shift_end:', schedule.shift_end);
        return 'present';
    }

    // Construct shiftStart and shiftEnd
    const date = moment.utc(schedule.date).format('YYYY-MM-DD');
    const shiftStart = moment.utc(`${date} ${schedule.shift_start}`, 'YYYY-MM-DD HH:mm:ss');
    const shiftEnd = moment.utc(`${date} ${schedule.shift_end}`, 'YYYY-MM-DD HH:mm:ss');
    const checkIn = moment.utc(check_in_time);

    // Validate constructed dates
    if (!shiftStart.isValid() || !shiftEnd.isValid()) {
        console.error('Invalid constructed dates:', {
            shiftStart: shiftStart.toString(),
            shiftEnd: shiftEnd.toString()
        });
        return 'present';
    }

    const expectedHours = moment.duration(shiftEnd.diff(shiftStart)).asHours() - (schedule.break_duration / 60);
    let status = 'present';

    //console.log('Calculating status:', {
    //    check_in_time,
    //    checkIn: checkIn.format('YYYY-MM-DD HH:mm:ss Z'),
    //    shiftStart: shiftStart.format('YYYY-MM-DD HH:mm:ss Z'),
    //    shiftEnd: shiftEnd.format('YYYY-MM-DD HH:mm:ss Z'),
    //    isLate: checkIn.isAfter(shiftStart.add(15, 'minutes')),
    //    expectedHours
    //});

    if (checkIn.isAfter(shiftStart)) {
        status = 'late';
    }

    if (check_out_time) {
        const checkOut = moment.utc(check_out_time);
        const earlyThreshold = moment(shiftEnd).subtract(15, 'minutes');
        // console.log('Check-out details:', {
        //     checkOut: checkOut.format('YYYY-MM-DD HH:mm:ss Z'),
        //     earlyThreshold: earlyThreshold.format('YYYY-MM-DD HH:mm:ss Z'),
        //     isEarly: checkOut.isBefore(earlyThreshold)
        // });
        if (checkOut.isBefore(earlyThreshold)) {
            status = status === 'late' ? 'late' : 'early_leave';
        }
    }

    //console.log('Final status:', status);
    return status;
};

exports.createAttendance = async (req, res) => {
    try {
        const { employee_id, date, check_in_time, check_out_time, notes } = req.body;
        const user = req.user;

        //console.log('Creating attendance for:', { employee_id, date, check_in_time, userRole: user.role });

        const employee = await checkEmployeeExistence(employee_id);
        if (!employee) {
            return res.status(400).json({ message: 'Nhân viên không tồn tại' });
        }
        if (employee.status === 'terminated') {
            return res.status(400).json({ message: 'Nhân viên đã nghỉ việc, không thể chấm công' });
        }
        if (employee.status === 'on_leave') {
            return res.status(400).json({ message: 'Nhân viên đang nghỉ phép, kiểm tra lại lịch chấm công' });
        }

        if (user.role === 'employee' && user.employee_id !== employee_id) {
            return res.status(403).json({ message: 'Không có quyền ghi nhận chấm công cho nhân viên khác' });
        }
        if (!['admin', 'hr', 'dep_manager'].includes(user.role) && user.employee_id !== employee_id) {
            return res.status(403).json({ message: 'Không có quyền ghi nhận chấm công' });
        }
        if (user.role === 'dep_manager') {
            const manager = await checkEmployeeExistence(user.employee_id);
            if (employee.department_id !== manager.department_id) {
                return res.status(403).json({ message: 'Không có quyền ghi nhận chấm công cho nhân viên ngoài phòng ban' });
            }
        }

        const attendanceDate = moment.utc(date).startOf('day');
        if (attendanceDate.isAfter(moment.utc().startOf('day'))) {
            return res.status(400).json({ message: 'Không thể tạo bản ghi chấm công cho ngày trong tương lai' });
        }
        if (user.role === 'employee' && !attendanceDate.isSame(moment.utc().startOf('day'))) {
            return res.status(400).json({ message: 'Nhân viên chỉ có thể chấm công cho ngày hiện tại' });
        }

        const schedule = await Attendance.getSchedule(employee_id, date);
        if (!schedule) {
            return res.status(400).json({ message: 'Không tìm thấy lịch làm việc xác nhận cho ngày này' });
        }

        if (check_out_time && moment.utc(check_out_time).isBefore(moment.utc(check_in_time))) {
            return res.status(400).json({ message: 'Thời gian check-out phải sau thời gian check-in' });
        }

        const status = user.role === 'employee' ? calculateStatus(check_in_time, check_out_time, schedule) : req.body.status || calculateStatus(check_in_time, check_out_time, schedule);

        const attendanceData = { employee_id, date, check_in_time, check_out_time, status, notes: user.role === 'employee' ? null : notes };
        const newAttendance = await Attendance.create(attendanceData);

        if (!newAttendance) {
            return res.status(400).json({ message: 'Không thể tạo bản ghi chấm công' });
        }

        if (['late', 'early_leave', 'absent'].includes(status)) {
            const message = `Chấm công ngày ${newAttendance.date}: ${status === 'late' ? 'Đến muộn' : status === 'early_leave' ? 'Rời sớm' : 'Vắng mặt'}`;
            await Attendance.createNotification(
                employee.user_id,
                'Thông Báo Chấm Công',
                message,
                'attendance'
            );

            const [hrUsers] = await db.execute(`SELECT id FROM users WHERE role = 'hr'`);
            if (hrUsers.length > 0) {
                const notificationValues = hrUsers.map(hr => [
                    hr.id,
                    'Thông Báo Chấm Công Nhân Viên',
                    `${employee.first_name} ${employee.last_name} ${status === 'late' ? 'đến muộn' : status === 'early_leave' ? 'rời sớm' : 'vắng mặt'} ngày ${newAttendance.date}`,
                    'attendance'
                ]);
                await db.execute(
                    `INSERT INTO notifications (user_id, title, message, type) VALUES ?`,
                    [notificationValues]
                );
            }
        }

        return res.status(201).json({ message: 'Ghi nhận chấm công thành công', attendance: newAttendance });
    } catch (error) {
        console.error('Lỗi khi tạo chấm công:', error);
        return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
    }
};

exports.getAttendance = async (req, res) => {
    try {
        const { employee_id, start_date, end_date, status, department_id, page, limit } = req.query;
        const user = req.user;

        let filters = { page: parseInt(page) || 1, limit: parseInt(limit) || 10 };
        if (employee_id) filters.employee_id = parseInt(employee_id);
        if (start_date) filters.start_date = start_date;
        if (end_date) filters.end_date = end_date;
        if (status) filters.status = status;
        if (department_id) filters.department_id = parseInt(department_id);

        if (user.role === 'employee') {
            filters.employee_id = user.employee_id;
        } else if (user.role === 'dep_manager') {
            const manager = await checkEmployeeExistence(user.employee_id);
            filters.department_id = manager.department_id;
        }

        const result = await Attendance.getAll(filters);
        return res.status(200).json({
            message: 'Lấy danh sách chấm công thành công',
            ...result
        });
    } catch (error) {
        console.error('Lỗi khi lấy danh sách chấm công:', error);
        return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
    }
};

exports.updateAttendance = async (req, res) => {
    try {
        const { id } = req.params;
        const { check_in_time, check_out_time, status: providedStatus, notes } = req.body;
        const user = req.user;

        //console.log('Updating attendance ID:', id, 'with data:', req.body);

        if (!['admin', 'hr', 'dep_manager'].includes(user.role)) {
            return res.status(403).json({ message: 'Không có quyền cập nhật chấm công' });
        }

        const attendance = await Attendance.findById(id);
        if (!attendance) {
            return res.status(404).json({ message: 'Không tìm thấy bản ghi chấm công' });
        }

        const employee = await checkEmployeeExistence(attendance.employee_id);
        if (!employee) {
            return res.status(400).json({ message: 'Nhân viên không tồn tại' });
        }
        if (employee.status === 'terminated') {
            return res.status(400).json({ message: 'Nhân viên đã nghỉ việc, không thể cập nhật chấm công' });
        }
        if (employee.status === 'on_leave') {
            return res.status(400).json({ message: 'Nhân viên đang nghỉ phép, kiểm tra lại lịch chấm công' });
        }

        if (user.role === 'dep_manager' && employee.department_id !== (await checkEmployeeExistence(user.employee_id))?.department_id) {
            return res.status(403).json({ message: 'Không có quyền cập nhật chấm công cho nhân viên ngoài phòng ban' });
        }

        const schedule = await Attendance.getSchedule(attendance.employee_id, attendance.date);
        if (!schedule) {
            return res.status(400).json({ message: 'Không tìm thấy lịch làm việc xác nhận cho ngày này' });
        }

        if (moment.utc(attendance.date).isBefore(moment.utc().startOf('day'))) {
            return res.status(400).json({ message: 'Không thể cập nhật chấm công cho ngày trong quá khứ' });
        }

        if (check_out_time && moment.utc(check_out_time).isBefore(moment.utc(check_in_time || attendance.check_in_time))) {
            return res.status(400).json({ message: 'Thời gian check-out phải sau thời gian check-in' });
        }

        const status = providedStatus || calculateStatus(
            check_in_time || attendance.check_in_time,
            check_out_time || attendance.check_out_time,
            schedule
        );

        const updateData = { check_in_time, check_out_time, status, notes };
        //console.log('Update attendance data:', updateData);
        const isUpdated = await Attendance.update(id, updateData);

        if (!isUpdated) {
            return res.status(400).json({ message: 'Không có thay đổi hoặc cập nhật thất bại' });
        }

        const updatedAttendance = await Attendance.findById(id);
        const significantChanges = (
            (providedStatus && providedStatus !== attendance.status) ||
            (check_in_time && moment.utc(check_in_time).diff(moment.utc(attendance.check_in_time), 'minutes') > 30) ||
            (check_out_time && attendance.check_out_time && moment.utc(check_out_time).diff(moment.utc(attendance.check_out_time), 'minutes') > 30)
        );

        if (significantChanges) {
            const message = `Cập nhật chấm công ngày ${updatedAttendance.date}: ${status === 'late' ? 'Đến muộn' : status === 'early_leave' ? 'Rời sớm' : status === 'absent' ? 'Vắng mặt' : 'Thay đổi thời gian'}`;
            await Attendance.createNotification(
                employee.user_id,
                'Cập Nhật Chấm Công',
                message,
                'attendance'
            );

            const [hrUsers] = await db.execute(`SELECT id FROM users WHERE role = 'hr'`);
            if (hrUsers.length > 0) {
                const notificationValues = hrUsers.map(hr => [
                    hr.id,
                    'Cập Nhật Chấm Công Nhân Viên',
                    `${employee.first_name} ${employee.last_name} cập nhật chấm công ngày ${updatedAttendance.date}`,
                    'attendance'
                ]);
                await db.execute(
                    `INSERT INTO notifications (user_id, title, message, type) VALUES ?`,
                    [notificationValues]
                );
            }
        }

        return res.status(200).json({ message: 'Cập nhật chấm công thành công', attendance: updatedAttendance });
    } catch (error) {
        console.error('Lỗi khi cập nhật chấm công:', error);
        return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
    }
};

exports.getAttendanceReport = async (req, res) => {
    try {
        const { employee_id, department_id, start_date, end_date } = req.query;
        const user = req.user;

        let filters = {};
        if (employee_id) filters.employee_id = parseInt(employee_id);
        if (department_id) filters.department_id = parseInt(department_id);
        if (start_date) filters.start_date = start_date;
        if (end_date) filters.end_date = end_date;

        if (user.role === 'dep_manager') {
            const manager = await checkEmployeeExistence(user.employee_id);
            filters.department_id = manager.department_id;
        } else if (!['admin', 'hr'].includes(user.role)) {
            return res.status(403).json({ message: 'Không có quyền xem báo cáo chấm công' });
        }

        let query = `
            SELECT 
                e.id as employee_id,
                CONCAT(e.first_name, ' ', e.last_name) as employee_name,
                d.name as department_name,
                COUNT(a.id) as total_days,
                COALESCE(SUM(a.work_hours), 0) as total_work_hours,
                COALESCE(AVG(a.work_hours), 0) as avg_work_hours_per_day,
                SUM(CASE WHEN a.status = 'late' THEN 1 ELSE 0 END) as late_count,
                SUM(CASE WHEN a.status = 'early_leave' THEN 1 ELSE 0 END) as early_leave_count,
                SUM(CASE WHEN a.status = 'absent' THEN 1 ELSE 0 END) as absent_count,
                COALESCE(SUM(CASE 
                    WHEN a.work_hours > (
                        TIMESTAMPDIFF(SECOND, s.shift_start, s.shift_end) / 3600 - (s.break_duration / 60)
                    ) THEN a.work_hours - (
                        TIMESTAMPDIFF(SECOND, s.shift_start, s.shift_end) / 3600 - (s.break_duration / 60)
                    ) 
                    ELSE 0 
                END), 0) as overtime_hours
            FROM employees e
            LEFT JOIN attendance a ON e.id = a.employee_id
            LEFT JOIN schedules s ON a.employee_id = s.employee_id AND a.date = s.date AND s.status = 'confirmed'
            LEFT JOIN departments d ON e.department_id = d.id
            WHERE 1=1
        `;
        const values = [];

        if (filters.employee_id) {
            query += ' AND e.id = ?';
            values.push(filters.employee_id);
        }
        if (filters.department_id) {
            query += ' AND e.department_id = ?';
            values.push(filters.department_id);
        }
        if (filters.start_date) {
            query += ' AND a.date >= ?';
            values.push(moment.utc(filters.start_date).format('YYYY-MM-DD'));
        }
        if (filters.end_date) {
            query += ' AND a.date <= ?';
            values.push(moment.utc(filters.end_date).format('YYYY-MM-DD'));
        }

        query += ' GROUP BY e.id, e.first_name, e.last_name, d.name HAVING total_days > 0';
        const [employeeBreakdown] = await db.execute(query, values);

        query = `
            SELECT 
                d.id as department_id,
                d.name as department_name,
                COUNT(a.id) as total_days,
                COALESCE(SUM(a.work_hours), 0) as total_work_hours,
                COALESCE(AVG(a.work_hours), 0) as avg_work_hours_per_day,
                SUM(CASE WHEN a.status = 'late' THEN 1 ELSE 0 END) as late_count,
                SUM(CASE WHEN a.status = 'early_leave' THEN 1 ELSE 0 END) as early_leave_count,
                SUM(CASE WHEN a.status = 'absent' THEN 1 ELSE 0 END) as absent_count
            FROM departments d
            LEFT JOIN employees e ON d.id = e.department_id
            LEFT JOIN attendance a ON e.id = a.employee_id
            WHERE 1=1
        `;
        const departmentValues = [];

        if (filters.department_id) {
            query += ' AND d.id = ?';
            departmentValues.push(filters.department_id);
        }
        if (filters.start_date) {
            query += ' AND a.date >= ?';
            departmentValues.push(moment.utc(filters.start_date).format('YYYY-MM-DD'));
        }
        if (filters.end_date) {
            query += ' AND a.date <= ?';
            departmentValues.push(moment.utc(filters.end_date).format('YYYY-MM-DD'));
        }

        query += ' GROUP BY d.id, d.name HAVING total_days > 0';
        const [departmentBreakdown] = await db.execute(query, departmentValues);

        query = `
            SELECT 
                COUNT(id) as total_days,
                COALESCE(SUM(work_hours), 0) as total_work_hours,
                COALESCE(AVG(work_hours), 0) as avg_work_hours_per_day
            FROM attendance
            WHERE 1=1
        `;
        const overallValues = [];

        if (filters.start_date) {
            query += ' AND date >= ?';
            overallValues.push(moment.utc(filters.start_date).format('YYYY-MM-DD'));
        }
        if (filters.end_date) {
            query += ' AND date <= ?';
            overallValues.push(moment.utc(filters.end_date).format('YYYY-MM-DD'));
        }

        const [overallMetrics] = await db.execute(query, overallValues);

        return res.status(200).json({
            message: 'Báo cáo chấm công được tạo thành công',
            report: {
                employee_breakdown: employeeBreakdown,
                department_breakdown: departmentBreakdown,
                overall_metrics: overallMetrics[0]
            }
        });
    } catch (error) {
        console.error('Lỗi khi tạo báo cáo chấm công:', error);
        return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
    }
};