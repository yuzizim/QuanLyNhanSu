// evaluationController.js

// const Evaluation = require('../models/evaluationModel');
// const Notification = require('../models/notificationModel');
// const db = require('../config/db');
// const moment = require('moment-timezone');

// const checkEmployeeExistence = async (employeeId) => {
//     const [rows] = await db.execute(
//         'SELECT id, status, user_id, first_name, last_name, department_id FROM employees WHERE id = ?',
//         [employeeId]
//     );
//     return rows[0] || null;
// };

// exports.getEvaluations = async (req, res) => {
//     try {
//         const { employee_id, evaluator_id, start_date, end_date, status, department_id, page, limit } = req.query;
//         const user = req.user;

//         let filters = { page: parseInt(page) || 1, limit: parseInt(limit) || 10 };
//         if (employee_id) filters.employee_id = parseInt(employee_id);
//         if (evaluator_id) filters.evaluator_id = parseInt(evaluator_id);
//         if (start_date) filters.start_date = start_date;
//         if (end_date) filters.end_date = end_date;
//         if (status) filters.status = status;
//         if (department_id) filters.department_id = parseInt(department_id);

//         if (user.role === 'employee') {
//             filters.employee_id = user.employee_id;
//         } else if (user.role === 'dep_manager') {
//             filters.department_id = user.department_id;
//         }

//         const result = await Evaluation.getAll(filters);
//         return res.status(200).json({
//             message: 'Lấy danh sách đánh giá thành công',
//             ...result
//         });
//     } catch (error) {
//         console.error('Lỗi khi lấy danh sách đánh giá:', error);
//         return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
//     }
// };

// exports.createEvaluation = async (req, res) => {
//     try {
//         const {
//             employee_id, evaluator_id, evaluation_date, performance_score,
//             kpi_score, overall_score, strengths, improvement_areas, comments, status
//         } = req.body;
//         const user = req.user;

//         if (!['admin', 'hr', 'dep_manager'].includes(user.role)) {
//             return res.status(403).json({ message: 'Không có quyền tạo đánh giá' });
//         }

//         const employee = await checkEmployeeExistence(employee_id);
//         if (!employee) {
//             return res.status(400).json({ message: 'Nhân viên không tồn tại' });
//         }
//         if (employee.status !== 'active') {
//             return res.status(400).json({ message: 'Nhân viên không ở trạng thái hoạt động' });
//         }

//         const evaluator = await checkEmployeeExistence(evaluator_id);
//         if (!evaluator) {
//             return res.status(400).json({ message: 'Người đánh giá không tồn tại' });
//         }

//         if (user.role === 'dep_manager' && employee.department_id !== user.department_id) {
//             return res.status(403).json({ message: 'Không có quyền đánh giá nhân viên ngoài phòng ban' });
//         }

//         if (moment.utc(evaluation_date).isAfter(moment.utc())) {
//             return res.status(400).json({ message: 'Ngày đánh giá không được trong tương lai' });
//         }

//         const evaluationData = {
//             employee_id, evaluator_id, evaluation_date, performance_score,
//             kpi_score, overall_score, strengths, improvement_areas, comments, status
//         };
//         const newEvaluation = await Evaluation.create(evaluationData);

//         if (!newEvaluation) {
//             return res.status(400).json({ message: 'Không thể tạo đánh giá' });
//         }

//         if (status === 'finalized') {
//             await Notification.create({
//                 user_id: employee.user_id,
//                 title: 'Đánh Giá Hiệu Suất',
//                 message: `Đánh giá ngày ${newEvaluation.evaluation_date} đã được hoàn tất với điểm tổng thể ${overall_score}`,
//                 type: 'evaluation'
//             });
//         }

//         return res.status(201).json({ message: 'Tạo đánh giá thành công', evaluation: newEvaluation });
//     } catch (error) {
//         console.error('Lỗi khi tạo đánh giá:', error);
//         return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
//     }
// };

// exports.updateEvaluation = async (req, res) => {
//     try {
//         const { id } = req.params;
//         const {
//             evaluation_date, performance_score, kpi_score, overall_score,
//             strengths, improvement_areas, comments, status
//         } = req.body;
//         const user = req.user;

//         if (!['admin', 'hr', 'dep_manager'].includes(user.role)) {
//             return res.status(403).json({ message: 'Không có quyền cập nhật đánh giá' });
//         }

//         const evaluation = await Evaluation.findById(id);
//         if (!evaluation) {
//             return res.status(404).json({ message: 'Không tìm thấy đánh giá' });
//         }

//         const employee = await checkEmployeeExistence(evaluation.employee_id);
//         if (!employee) {
//             return res.status(400).json({ message: 'Nhân viên không tồn tại' });
//         }

//         if (user.role === 'dep_manager' && employee.department_id !== user.department_id) {
//             return res.status(403).json({ message: 'Không có quyền cập nhật đánh giá cho nhân viên ngoài phòng ban' });
//         }

//         if (evaluation.status === 'finalized') {
//             return res.status(400).json({ message: 'Không thể cập nhật đánh giá đã hoàn tất' });
//         }

//         const updateData = {
//             evaluation_date, performance_score, kpi_score, overall_score,
//             strengths, improvement_areas, comments, status
//         };
//         const isUpdated = await Evaluation.update(id, updateData);

//         if (!isUpdated) {
//             return res.status(400).json({ message: 'Không có thay đổi hoặc cập nhật thất bại' });
//         }

//         const updatedEvaluation = await Evaluation.findById(id);
//         if (status === 'finalized' && evaluation.status !== 'finalized') {
//             await Notification.create({
//                 user_id: employee.user_id,
//                 title: 'Cập Nhật Đánh Giá Hiệu Suất',
//                 message: `Đánh giá ngày ${updatedEvaluation.evaluation_date} đã được hoàn tất với điểm tổng thể ${updatedEvaluation.overall_score}`,
//                 type: 'evaluation'
//             });
//         }

//         return res.status(200).json({ message: 'Cập nhật đánh giá thành công', evaluation: updatedEvaluation });
//     } catch (error) {
//         console.error('Lỗi khi cập nhật đánh giá:', error);
//         return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
//     }
// };

// exports.deleteEvaluation = async (req, res) => {
//     try {
//         const { id } = req.params;
//         const user = req.user;

//         if (!['admin', 'hr'].includes(user.role)) {
//             return res.status(403).json({ message: 'Không có quyền xóa đánh giá' });
//         }

//         const evaluation = await Evaluation.findById(id);
//         if (!evaluation) {
//             return res.status(404).json({ message: 'Không tìm thấy đánh giá' });
//         }

//         const isDeleted = await Evaluation.delete(id);
//         if (!isDeleted) {
//             return res.status(400).json({ message: 'Xóa đánh giá thất bại' });
//         }

//         return res.status(200).json({ message: 'Xóa đánh giá thành công' });
//     } catch (error) {
//         console.error('Lỗi khi xóa đánh giá:', error);
//         return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
//     }
// };



const Evaluation = require('../models/evaluationModel');
const Notification = require('../models/notificationModel');
const db = require('../config/db');
const moment = require('moment-timezone');

const checkEmployeeExistence = async (employeeId) => {
    const [rows] = await db.execute(
        'SELECT id, status, user_id, first_name, last_name, department_id FROM employees WHERE id = ?',
        [employeeId]
    );
    return rows[0] || null;
};

exports.getEvaluations = async (req, res) => {
    try {
        const { employee_id, evaluator_id, start_date, end_date, status, department_id, page, limit } = req.query;
        const user = req.user;

        let filters = { page: parseInt(page) || 1, limit: parseInt(limit) || 10 };
        if (employee_id) filters.employee_id = parseInt(employee_id);
        if (evaluator_id) filters.evaluator_id = parseInt(evaluator_id);
        if (start_date) filters.start_date = start_date;
        if (end_date) filters.end_date = end_date;
        if (status) filters.status = status;
        if (department_id) filters.department_id = parseInt(department_id);

        if (user.role === 'employee') {
            filters.employee_id = user.employee_id;
        } else if (user.role === 'dep_manager') {
            filters.department_id = user.department_id;
        }

        const result = await Evaluation.getAll(filters);
        return res.status(200).json({
            message: 'Lấy danh sách đánh giá thành công',
            ...result
        });
    } catch (error) {
        console.error('Lỗi khi lấy danh sách đánh giá:', error);
        return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
    }
};

exports.createEvaluation = async (req, res) => {
    try {
        const {
            employee_id, performance_score, kpi_score, overall_score,
            strengths, improvement_areas, comments, status
        } = req.body;
        const user = req.user;

        if (!['admin', 'hr', 'dep_manager'].includes(user.role)) {
            return res.status(403).json({ message: 'Không có quyền tạo đánh giá' });
        }

        const employee = await checkEmployeeExistence(employee_id);
        if (!employee) {
            return res.status(400).json({ message: 'Nhân viên không tồn tại' });
        }
        if (employee.status !== 'active') {
            return res.status(400).json({ message: 'Nhân viên không ở trạng thái hoạt động' });
        }

        const evaluator = await checkEmployeeExistence(user.employee_id);
        if (!evaluator) {
            return res.status(400).json({ message: 'Người đánh giá không tồn tại' });
        }

        if (user.role === 'dep_manager' && employee.department_id !== user.department_id) {
            return res.status(403).json({ message: 'Không có quyền đánh giá nhân viên ngoài phòng ban' });
        }

        const evaluationData = {
            employee_id,
            evaluator_id: user.employee_id,
            evaluation_date: moment.utc().format('YYYY-MM-DD'),
            performance_score,
            kpi_score,
            overall_score,
            strengths,
            improvement_areas,
            comments,
            status
        };
        console.log('Creating evaluation with data:', evaluationData);
        const newEvaluation = await Evaluation.create(evaluationData);

        if (!newEvaluation) {
            return res.status(400).json({ message: 'Không thể tạo đánh giá' });
        }

        if (status === 'finalized') {
            await Notification.create({
                user_id: employee.user_id,
                title: 'Đánh Giá Hiệu Suất',
                message: `Đánh giá ngày ${newEvaluation.evaluation_date} đã được hoàn tất với điểm tổng thể ${overall_score}`,
                type: 'evaluation'
            });
        }

        return res.status(201).json({ message: 'Tạo đánh giá thành công', evaluation: newEvaluation });
    } catch (error) {
        console.error('Lỗi khi tạo đánh giá:', error);
        return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
    }
};

exports.updateEvaluation = async (req, res) => {
    try {
        const { id } = req.params;
        const {
            evaluation_date, performance_score, kpi_score, overall_score,
            strengths, improvement_areas, comments, status
        } = req.body;
        const user = req.user;

        if (!['admin', 'hr', 'dep_manager'].includes(user.role)) {
            return res.status(403).json({ message: 'Không có quyền cập nhật đánh giá' });
        }

        const evaluation = await Evaluation.findById(id);
        if (!evaluation) {
            return res.status(404).json({ message: 'Không tìm thấy đánh giá' });
        }

        const employee = await checkEmployeeExistence(evaluation.employee_id);
        if (!employee) {
            return res.status(400).json({ message: 'Nhân viên không tồn tại' });
        }

        if (user.role === 'dep_manager' && employee.department_id !== user.department_id) {
            return res.status(403).json({ message: 'Không có quyền cập nhật đánh giá cho nhân viên ngoài phòng ban' });
        }

        if (evaluation.status === 'finalized') {
            return res.status(400).json({ message: 'Không thể cập nhật đánh giá đã hoàn tất' });
        }

        const updateData = {
            evaluation_date, performance_score, kpi_score, overall_score,
            strengths, improvement_areas, comments, status
        };
        const isUpdated = await Evaluation.update(id, updateData);

        if (!isUpdated) {
            return res.status(400).json({ message: 'Không có thay đổi hoặc cập nhật thất bại' });
        }

        const updatedEvaluation = await Evaluation.findById(id);
        if (status === 'finalized' && evaluation.status !== 'finalized') {
            await Notification.create({
                user_id: employee.user_id,
                title: 'Cập Nhật Đánh Giá Hiệu Suất',
                message: `Đánh giá ngày ${updatedEvaluation.evaluation_date} đã được hoàn tất với điểm tổng thể ${updatedEvaluation.overall_score}`,
                type: 'evaluation'
            });
        }

        return res.status(200).json({ message: 'Cập nhật đánh giá thành công', evaluation: updatedEvaluation });
    } catch (error) {
        console.error('Lỗi khi cập nhật đánh giá:', error);
        return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
    }
};

exports.deleteEvaluation = async (req, res) => {
    try {
        const { id } = req.params;
        const user = req.user;

        if (!['admin', 'hr'].includes(user.role)) {
            return res.status(403).json({ message: 'Không có quyền xóa đánh giá' });
        }

        const evaluation = await Evaluation.findById(id);
        if (!evaluation) {
            return res.status(404).json({ message: 'Không tìm thấy đánh giá' });
        }

        const isDeleted = await Evaluation.delete(id);
        if (!isDeleted) {
            return res.status(400).json({ message: 'Xóa đánh giá thất bại' });
        }

        return res.status(200).json({ message: 'Xóa đánh giá thành công' });
    } catch (error) {
        console.error('Lỗi khi xóa đánh giá:', error);
        return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
    }
};