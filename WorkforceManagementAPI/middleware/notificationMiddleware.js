// notificationMiddleware.js

const Joi = require('joi');

const createNotificationSchema = Joi.object({
    user_id: Joi.number().integer().positive().required().messages({
        'number.base': 'ID người dùng phải là một số',
        'number.integer': 'ID người dùng phải là số nguyên',
        'number.positive': 'ID người dùng phải là số dương',
        'any.required': 'ID người dùng là bắt buộc'
    }),
    title: Joi.string().max(255).required().messages({
        'string.base': 'Tiêu đề phải là một chuỗi ký tự',
        'string.max': 'Tiêu đề không được vượt quá 255 ký tự',
        'any.required': 'Tiêu đề là bắt buộc'
    }),
    message: Joi.string().max(1000).required().messages({
        'string.base': 'Thông điệp phải là một chuỗi ký tự',
        'string.max': 'Thông điệp không được vượt quá 1000 ký tự',
        'any.required': 'Thông điệp là bắt buộc'
    }),
    type: Joi.string().valid('task', 'attendance', 'evaluation', 'announcement', 'system').required().messages({
        'any.only': 'Loại thông báo phải là một trong: task, attendance, evaluation, announcement, system',
        'any.required': 'Loại thông báo là bắt buộc'
    })
});

const updateNotificationSchema = Joi.object({
    title: Joi.string().max(255).messages({
        'string.base': 'Tiêu đề phải là một chuỗi ký tự',
        'string.max': 'Tiêu đề không được vượt quá 255 ký tự'
    }),
    message: Joi.string().max(1000).messages({
        'string.base': 'Thông điệp phải là một chuỗi ký tự',
        'string.max': 'Thông điệp không được vượt quá 1000 ký tự'
    }),
    type: Joi.string().valid('task', 'attendance', 'evaluation', 'announcement', 'system').messages({
        'any.only': 'Loại thông báo phải là một trong: task, attendance, evaluation, announcement, system'
    }),
    is_read: Joi.boolean().messages({
        'boolean.base': 'is_read phải là một giá trị boolean'
    })
}).min(1).messages({
    'object.min': 'Phải cung cấp ít nhất một trường để cập nhật'
});

exports.createNotificationValidator = async (req, res, next) => {
    try {
        console.log('Validating create notification:', req.body);
        await createNotificationSchema.validateAsync(req.body, { abortEarly: false });
        next();
    } catch (error) {
        console.error('Validation errors:', error.details);
        return res.status(400).json({
            message: 'Lỗi xác thực dữ liệu',
            errors: error.details.map(err => err.message)
        });
    }
};

exports.updateNotificationValidator = async (req, res, next) => {
    try {
        console.log('Validating update notification:', req.body);
        await updateNotificationSchema.validateAsync(req.body, { abortEarly: false });
        next();
    } catch (error) {
        console.error('Validation errors:', error.details);
        return res.status(400).json({
            message: 'Lỗi xác thực dữ liệu',
            errors: error.details.map(err => err.message)
        });
    }
};