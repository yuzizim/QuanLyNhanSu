// attendanceMiddleware.js

const Joi = require('joi');
const moment = require('moment-timezone');

const createAttendanceSchema = Joi.object({
    employee_id: Joi.number().integer().positive().required().messages({
        'number.base': 'ID nhân viên phải là một số',
        'number.integer': 'ID nhân viên phải là số nguyên',
        'number.positive': 'ID nhân viên phải là số dương',
        'any.required': 'ID nhân viên là bắt buộc'
    }),
    date: Joi.date().iso().max('now').required().messages({
        'date.base': 'Ngày phải là một ngày hợp lệ (định dạng ISO)',
        'date.format': 'Ngày phải theo định dạng ISO',
        'date.max': 'Ngày không được là ngày trong tương lai',
        'any.required': 'Ngày là bắt buộc'
    }),
    check_in_time: Joi.date().iso().required().messages({
        'date.base': 'Thời gian check-in phải là một thời điểm hợp lệ (định dạng ISO)',
        'date.format': 'Thời gian check-in phải theo định dạng ISO',
        'any.required': 'Thời gian check-in là bắt buộc'
    }),
    check_out_time: Joi.date().iso().allow(null).greater(Joi.ref('check_in_time')).messages({
        'date.base': 'Thời gian check-out phải là một thời điểm hợp lệ (định dạng ISO)',
        'date.format': 'Thời gian check-out phải theo định dạng ISO',
        'date.greater': 'Thời gian check-out phải sau thời gian check-in'
    }),
    status: Joi.string().valid('present', 'absent', 'late', 'early_leave').messages({
        'any.only': 'Trạng thái phải là một trong: present, absent, late, early_leave',
        'string.base': 'Trạng thái phải là một chuỗi ký tự'
    }),
    notes: Joi.string().max(1000).allow(null, '').messages({
        'string.base': 'Ghi chú phải là một chuỗi ký tự',
        'string.max': 'Ghi chú không được vượt quá 1000 ký tự'
    })
});

const updateAttendanceSchema = Joi.object({
    check_in_time: Joi.date().iso().messages({
        'date.base': 'Thời gian check-in phải là một thời điểm hợp lệ (định dạng ISO)',
        'date.format': 'Thời gian check-in phải theo định dạng ISO'
    }),
    check_out_time: Joi.date().iso().allow(null).messages({
        'date.base': 'Thời gian check-out phải là một thời điểm hợp lệ (định dạng ISO)',
        'date.format': 'Thời gian check-out phải theo định dạng ISO'
    }),
    status: Joi.string().valid('present', 'absent', 'late', 'early_leave').messages({
        'any.only': 'Trạng thái phải là một trong: present, absent, late, early_leave',
        'string.base': 'Trạng thái phải là một chuỗi ký tự'
    }),
    notes: Joi.string().max(1000).allow(null, '').messages({
        'string.base': 'Ghi chú phải là một chuỗi ký tự',
        'string.max': 'Ghi chú không được vượt quá 1000 ký tự'
    })
}).min(1).messages({
    'object.min': 'Phải cung cấp ít nhất một trường để cập nhật'
});

exports.createAttendanceValidator = async (req, res, next) => {
    try {
        console.log('Validating create attendance:', req.body);
        await createAttendanceSchema.validateAsync(req.body, { abortEarly: false });
        next();
    } catch (error) {
        console.error('Validation errors:', error.details);
        return res.status(400).json({
            message: 'Lỗi xác thực dữ liệu',
            errors: error.details.map(err => err.message)
        });
    }
};

exports.updateAttendanceValidator = async (req, res, next) => {
    try {
        console.log('Validating update attendance:', req.body);
        await updateAttendanceSchema.validateAsync(req.body, { abortEarly: false });
        next();
    } catch (error) {
        console.error('Validation errors:', error.details);
        return res.status(400).json({
            message: 'Lỗi xác thực dữ liệu',
            errors: error.details.map(err => err.message)
        });
    }
};