// scheduleController.js

const Schedule = require('../models/scheduleModel');
const Employee = require('../models/employeeModel');
const db = require('../config/db.js');
const moment = require('moment');

const checkEmployeeExistence = async (employeeId) => {
    const [rows] = await db.execute(
        'SELECT id, status, user_id, first_name, last_name, department_id FROM employees WHERE id = ?',
        [employeeId]
    );
    return rows[0] || null;
};

exports.createSchedule = async (req, res) => {
    try {
        if (!req.body) {
            return res.status(400).json({ message: 'Yêu cầu không có nội dung' });
        }
        const { employee_id, date, shift_start, shift_end, break_duration, status } = req.body;
        const user = req.user;

        if (!['admin', 'hr', 'dep_manager'].includes(user.role)) {
            return res.status(403).json({ message: 'Không có quyền tạo lịch làm việc' });
        }

        const employee = await checkEmployeeExistence(employee_id);
        if (!employee) {
            return res.status(400).json({ message: 'Nhân viên không tồn tại' });
        }
        if (employee.status === 'terminated') {
            return res.status(400).json({ message: 'Nhân viên đã nghỉ việc, không thể tạo lịch' });
        }

        if (user.role === 'dep_manager' && employee.department_id !== (await checkEmployeeExistence(user.employee_id))?.department_id) {
            return res.status(403).json({ message: 'Không có quyền tạo lịch cho nhân viên ngoài phòng ban' });
        }

        const scheduleDate = moment(date).startOf('day');
        if (scheduleDate.isBefore(moment().startOf('day'))) {
            return res.status(400).json({ message: 'Không thể tạo lịch cho ngày trong quá khứ' });
        }
        if (moment(`${date} ${shift_end}`).isSameOrBefore(moment(`${date} ${shift_start}`))) {
            return res.status(400).json({ message: 'Thời gian kết thúc ca phải sau thời gian bắt đầu' });
        }

        if (await Schedule.checkOverlap(employee_id, date, shift_start, shift_end)) {
            return res.status(400).json({ message: 'Lịch làm việc trùng với ca khác trong cùng ngày' });
        }

        const created_by = user.employee_id || employee_id;
        if (!created_by) {
            return res.status(400).json({ message: 'Không thể xác định người tạo lịch' });
        }

        const scheduleData = {
            employee_id, date, shift_start, shift_end, break_duration, status, created_by
        };
        const newSchedule = await Schedule.create(scheduleData);

        if (!newSchedule) {
            return res.status(400).json({ message: 'Không thể tạo lịch làm việc' });
        }

        if (newSchedule.status === 'confirmed') {
            const message = `Lịch làm việc ngày ${date}: Ca từ ${shift_start} đến ${shift_end}`;
            await Schedule.createNotification(
                employee.user_id,
                'Lịch Làm Việc Mới',
                message,
                'schedule'
            );

            const [hrUsers] = await db.execute(`SELECT id FROM users WHERE role = 'hr'`);
            if (hrUsers.length > 0) {
                const notificationValues = hrUsers.map(hr => [
                    hr.id,
                    'Lịch Làm Việc Nhân Viên',
                    `Lịch làm việc mới cho ${employee.first_name} ${employee.last_name} ngày ${date}`,
                    'schedule'
                ]);
                await db.execute(
                    `INSERT INTO notifications (user_id, title, message, type) VALUES ?`,
                    [notificationValues]
                );
            }
        }

        return res.status(201).json({ message: 'Tạo lịch làm việc thành công', schedule: newSchedule });
    } catch (error) {
        console.error('Lỗi khi tạo lịch làm việc:', error);
        return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
    }
};

exports.getSchedules = async (req, res) => {
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

        const result = await Schedule.getAll(filters);
        return res.status(200).json({
            message: 'Lấy danh sách lịch làm việc thành công',
            ...result
        });
    } catch (error) {
        console.error('Lỗi khi lấy danh sách lịch làm việc:', error);
        return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
    }
};

exports.updateSchedule = async (req, res) => {
    try {
        if (!req.body) {
            return res.status(400).json({ message: 'Yêu cầu không có nội dung' });
        }
        const { id } = req.params;
        const { employee_id, date, shift_start, shift_end, break_duration, status } = req.body;
        const user = req.user;

        if (!['admin', 'hr', 'dep_manager'].includes(user.role)) {
            return res.status(403).json({ message: 'Không có quyền cập nhật lịch làm việc' });
        }

        const schedule = await Schedule.findById(id);
        if (!schedule) {
            return res.status(404).json({ message: 'Không tìm thấy lịch làm việc' });
        }

        if (moment(schedule.date).isBefore(moment().startOf('day'))) {
            return res.status(400).json({ message: 'Không thể cập nhật lịch làm việc trong quá khứ' });
        }
        if (schedule.status === 'completed') {
            return res.status(400).json({ message: 'Không thể cập nhật lịch đã hoàn thành' });
        }

        let newEmployeeId = employee_id || schedule.employee_id;
        const employee = await checkEmployeeExistence(newEmployeeId);
        if (!employee) {
            return res.status(400).json({ message: 'Nhân viên không tồn tại' });
        }
        if (employee.status === 'terminated') {
            return res.status(400).json({ message: 'Nhân viên đã nghỉ việc, không thể cập nhật lịch' });
        }

        if (user.role === 'dep_manager' && employee.department_id !== (await checkEmployeeExistence(user.employee_id))?.department_id) {
            return res.status(403).json({ message: 'Không có quyền cập nhật lịch cho nhân viên ngoài phòng ban' });
        }

        const newDate = date ? moment(date).format('YYYY-MM-DD') : schedule.date;
        if (moment(newDate).isBefore(moment().startOf('day'))) {
            return res.status(400).json({ message: 'Không thể cập nhật lịch cho ngày trong quá khứ' });
        }
        const newShiftStart = shift_start || schedule.shift_start;
        const newShiftEnd = shift_end || schedule.shift_end;
        if (moment(`${newDate} ${newShiftEnd}`).isSameOrBefore(moment(`${newDate} ${newShiftStart}`))) {
            return res.status(400).json({ message: 'Thời gian kết thúc ca phải sau thời gian bắt đầu' });
        }

        if (await Schedule.checkOverlap(newEmployeeId, newDate, newShiftStart, newShiftEnd, id)) {
            return res.status(400).json({ message: 'Lịch làm việc trùng với ca khác trong cùng ngày' });
        }

        const updateData = { employee_id, date, shift_start, shift_end, break_duration, status };
        const isUpdated = await Schedule.update(id, updateData);

        if (!isUpdated) {
            return res.status(400).json({ message: 'Không có thay đổi hoặc cập nhật thất bại' });
        }

        const updatedSchedule = await Schedule.findById(id);
        const significantChanges = (
            (status && status !== schedule.status) ||
            (date && date !== schedule.date) ||
            (shift_start && shift_start !== schedule.shift_start) ||
            (shift_end && shift_end !== schedule.shift_end)
        );

        if (significantChanges) {
            const message = `Lịch làm việc ngày ${updatedSchedule.date} đã được cập nhật: Ca từ ${updatedSchedule.shift_start} đến ${updatedSchedule.shift_end}${status ? `, trạng thái: ${status}` : ''}`;
            await Schedule.createNotification(
                employee.user_id,
                'Cập Nhật Lịch Làm Việc',
                message,
                'schedule'
            );

            const [hrUsers] = await db.execute(`SELECT id FROM users WHERE role = 'hr'`);
            if (hrUsers.length > 0) {
                const notificationValues = hrUsers.map(hr => [
                    hr.id,
                    'Cập Nhật Lịch Làm Việc Nhân Viên',
                    `Lịch làm việc cho ${employee.first_name} ${employee.last_name} ngày ${updatedSchedule.date} đã được cập nhật`,
                    'schedule'
                ]);
                await db.execute(
                    `INSERT INTO notifications (user_id, title, message, type) VALUES ?`,
                    [notificationValues]
                );
            }
        }

        return res.status(200).json({ message: 'Cập nhật lịch làm việc thành công', schedule: updatedSchedule });
    } catch (error) {
        console.error('Lỗi khi cập nhật lịch làm việc:', error);
        return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
    }
};

exports.deleteSchedule = async (req, res) => {
    try {
        const { id } = req.params;
        const user = req.user;

        if (!['admin', 'hr'].includes(user.role)) {
            return res.status(403).json({ message: 'Không có quyền xóa lịch làm việc' });
        }

        const schedule = await Schedule.findById(id);
        if (!schedule) {
            return res.status(404).json({ message: 'Không tìm thấy lịch làm việc' });
        }

        if (moment(schedule.date).isBefore(moment().startOf('day'))) {
            return res.status(400).json({ message: 'Không thể xóa lịch làm việc trong quá khứ' });
        }
        if (schedule.status === 'completed') {
            return res.status(400).json({ message: 'Không thể xóa lịch đã hoàn thành' });
        }

        const isDeleted = await Schedule.delete(id);
        if (!isDeleted) {
            return res.status(400).json({ message: 'Xóa lịch làm việc thất bại' });
        }

        const employee = await checkEmployeeExistence(schedule.employee_id);
        if (employee) {
            await Schedule.createNotification(
                employee.user_id,
                'Xóa Lịch Làm Việc',
                `Lịch làm việc ngày ${schedule.date} đã bị xóa`,
                'schedule'
            );
        }

        return res.status(200).json({ message: 'Xóa lịch làm việc thành công' });
    } catch (error) {
        console.error('Lỗi khi xóa lịch làm việc:', error);
        return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
    }
};