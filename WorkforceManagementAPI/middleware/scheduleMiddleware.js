// scheduleMiddleware.js

const Joi = require('joi');

const createScheduleSchema = Joi.object({
    employee_id: Joi.number().integer().positive().required().messages({
        'number.base': 'ID nhân viên phải là một số',
        'number.integer': 'ID nhân viên phải là số nguyên',
        'number.positive': 'ID nhân viên phải là số dương',
        'any.required': 'ID nhân viên là bắt buộc'
    }),
    date: Joi.date().iso().required().messages({
        'date.base': 'Ngày phải là một ngày hợp lệ (định dạng ISO)',
        'date.format': 'Ngày phải theo định dạng ISO',
        'any.required': 'Ngày là bắt buộc'
    }),
    shift_start: Joi.string().pattern(/^([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$/).required().messages({
        'string.base': 'Thời gian bắt đầu ca phải là chuỗi',
        'string.pattern.base': 'Thời gian bắt đầu ca phải có định dạng HH:mm:ss',
        'any.required': 'Thời gian bắt đầu ca là bắt buộc'
    }),
    shift_end: Joi.string().pattern(/^([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$/).required().messages({
        'string.base': 'Thời gian kết thúc ca phải là chuỗi',
        'string.pattern.base': 'Thời gian kết thúc ca phải có định dạng HH:mm:ss',
        'any.required': 'Thời gian kết thúc ca là bắt buộc'
    }),
    break_duration: Joi.number().integer().min(0).default(60).messages({
        'number.base': 'Thời gian nghỉ phải là một số',
        'number.integer': 'Thời gian nghỉ phải là số nguyên',
        'number.min': 'Thời gian nghỉ không được nhỏ hơn 0'
    }),
    status: Joi.string().valid('draft', 'confirmed', 'completed').default('draft').messages({
        'any.only': 'Trạng thái phải là một trong: draft, confirmed, completed',
        'string.base': 'Trạng thái phải là một chuỗi'
    })
});

const updateScheduleSchema = Joi.object({
    employee_id: Joi.number().integer().positive().messages({
        'number.base': 'ID nhân viên phải là một số',
        'number.integer': 'ID nhân viên phải là số nguyên',
        'number.positive': 'ID nhân viên phải là số dương'
    }),
    date: Joi.date().iso().messages({
        'date.base': 'Ngày phải là một ngày hợp lệ (định dạng ISO)',
        'date.format': 'Ngày phải theo định dạng ISO'
    }),
    shift_start: Joi.string().pattern(/^([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$/).messages({
        'string.base': 'Thời gian bắt đầu ca phải là chuỗi',
        'string.pattern.base': 'Thời gian bắt đầu ca phải có định dạng HH:mm:ss'
    }),
    shift_end: Joi.string().pattern(/^([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$/).messages({
        'string.base': 'Thời gian kết thúc ca phải là chuỗi',
        'string.pattern.base': 'Thời gian kết thúc ca phải có định dạng HH:mm:ss'
    }),
    break_duration: Joi.number().integer().min(0).messages({
        'number.base': 'Thời gian nghỉ phải là một số',
        'number.integer': 'Thời gian nghỉ phải là số nguyên',
        'number.min': 'Thời gian nghỉ không được nhỏ hơn 0'
    }),
    status: Joi.string().valid('draft', 'confirmed', 'completed').messages({
        'any.only': 'Trạng thái phải là một trong: draft, confirmed, completed',
        'string.base': 'Trạng thái phải là một chuỗi'
    })
}).min(1).messages({
    'object.min': 'Phải cung cấp ít nhất một trường để cập nhật'
});

exports.createScheduleValidator = async (req, res, next) => {
    try {
        console.log('Validating create schedule:', req.body);
        await createScheduleSchema.validateAsync(req.body, { abortEarly: false });
        next();
    } catch (error) {
        console.error('Validation errors:', error.details);
        return res.status(400).json({
            message: 'Lỗi xác thực dữ liệu',
            errors: error.details.map(err => err.message)
        });
    }
};

exports.updateScheduleValidator = async (req, res, next) => {
    try {
        console.log('Validating update schedule:', req.body);
        await updateScheduleSchema.validateAsync(req.body, { abortEarly: false });
        next();
    } catch (error) {
        console.error('Validation errors:', error.details);
        return res.status(400).json({
            message: 'Lỗi xác thực dữ liệu',
            errors: error.details.map(err => err.message)
        });
    }
};