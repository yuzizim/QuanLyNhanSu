// evaluationMiddleware.js

const Joi = require('joi');
const moment = require('moment-timezone');

const createEvaluationSchema = Joi.object({
    employee_id: Joi.number().integer().positive().required().messages({
        'number.base': 'ID nhân viên phải là một số',
        'number.integer': 'ID nhân viên phải là số nguyên',
        'number.positive': 'ID nhân viên phải là số dương',
        'any.required': 'ID nhân viên là bắt buộc'
    }),
    performance_score: Joi.number().integer().min(0).max(100).required().messages({
        'number.base': 'Điểm hiệu suất phải là một số',
        'number.integer': 'Điểm hiệu suất phải là số nguyên',
        'number.min': 'Điểm hiệu suất phải từ 0 trở lên',
        'number.max': 'Điểm hiệu suất không được vượt quá 100',
        'any.required': 'Điểm hiệu suất là bắt buộc'
    }),
    kpi_score: Joi.number().integer().min(0).max(100).required().messages({
        'number.base': 'Điểm KPI phải là một số',
        'number.integer': 'Điểm KPI phải là số nguyên',
        'number.min': 'Điểm KPI phải từ 0 trở lên',
        'number.max': 'Điểm KPI không được vượt quá 100',
        'any.required': 'Điểm KPI là bắt buộc'
    }),
    overall_score: Joi.number().integer().min(0).max(100).required().messages({
        'number.base': 'Điểm tổng thể phải là một số',
        'number.integer': 'Điểm tổng thể phải là số nguyên',
        'number.min': 'Điểm tổng thể phải từ 0 trở lên',
        'number.max': 'Điểm tổng thể không được vượt quá 100',
        'any.required': 'Điểm tổng thể là bắt buộc'
    }),
    strengths: Joi.string().max(1000).allow(null, '').messages({
        'string.base': 'Điểm mạnh phải là một chuỗi ký tự',
        'string.max': 'Điểm mạnh không được vượt quá 1000 ký tự'
    }),
    improvement_areas: Joi.string().max(1000).allow(null, '').messages({
        'string.base': 'Khu vực cần cải thiện phải là một chuỗi ký tự',
        'string.max': 'Khu vực cần cải thiện không được vượt quá 1000 ký tự'
    }),
    comments: Joi.string().max(1000).allow(null, '').messages({
        'string.base': 'Bình luận phải là một chuỗi ký tự',
        'string.max': 'Bình luận không được vượt quá 1000 ký tự'
    }),
    status: Joi.string().valid('draft', 'submitted', 'approved', 'finalized').messages({
        'any.only': 'Trạng thái phải là một trong: draft, submitted, approved, finalized'
    })
});

const updateEvaluationSchema = Joi.object({
    evaluation_date: Joi.date().iso().messages({
        'date.base': 'Ngày đánh giá phải là một ngày hợp lệ (định dạng ISO)',
        'date.format': 'Ngày đánh giá phải theo định dạng ISO'
    }),
    performance_score: Joi.number().integer().min(0).max(100).messages({
        'number.base': 'Điểm hiệu suất phải là một số',
        'number.integer': 'Điểm hiệu suất phải là số nguyên',
        'number.min': 'Điểm hiệu suất phải từ 0 trở lên',
        'number.max': 'Điểm hiệu suất không được vượt quá 100'
    }),
    kpi_score: Joi.number().integer().min(0).max(100).messages({
        'number.base': 'Điểm KPI phải là một số',
        'number.integer': 'Điểm KPI phải là số nguyên',
        'number.min': 'Điểm KPI phải từ 0 trở lên',
        'number.max': 'Điểm KPI không được vượt quá 100'
    }),
    overall_score: Joi.number().integer().min(0).max(100).messages({
        'number.base': 'Điểm tổng thể phải là một số',
        'number.integer': 'Điểm tổng thể phải là số nguyên',
        'number.min': 'Điểm tổng thể phải từ 0 trở lên',
        'number.max': 'Điểm tổng thể không được vượt quá 100'
    }),
    strengths: Joi.string().max(1000).allow(null, '').messages({
        'string.base': 'Điểm mạnh phải là một chuỗi ký tự',
        'string.max': 'Điểm mạnh không được vượt quá 1000 ký tự'
    }),
    improvement_areas: Joi.string().max(1000).allow(null, '').messages({
        'string.base': 'Khu vực cần cải thiện phải là một chuỗi ký tự',
        'string.max': 'Khu vực cần cải thiện không được vượt quá 1000 ký tự'
    }),
    comments: Joi.string().max(1000).allow(null, '').messages({
        'string.base': 'Bình luận phải là một chuỗi ký tự',
        'string.max': 'Bình luận không được vượt quá 1000 ký tự'
    }),
    status: Joi.string().valid('draft', 'submitted', 'approved', 'finalized').messages({
        'any.only': 'Trạng thái phải là một trong: draft, submitted, approved, finalized'
    })
}).min(1).messages({
    'object.min': 'Phải cung cấp ít nhất một trường để cập nhật'
});

exports.createEvaluationValidator = async (req, res, next) => {
    try {
        console.log('Validating create evaluation:', req.body);
        await createEvaluationSchema.validateAsync(req.body, { abortEarly: false });
        next();
    } catch (error) {
        console.error('Validation errors:', error.details);
        return res.status(400).json({
            message: 'Lỗi xác thực dữ liệu',
            errors: error.details.map(err => err.message)
        });
    }
};

exports.updateEvaluationValidator = async (req, res, next) => {
    try {
        console.log('Validating update evaluation:', req.body);
        await updateEvaluationSchema.validateAsync(req.body, { abortEarly: false });
        next();
    } catch (error) {
        console.error('Validation errors:', error.details);
        return res.status(400).json({
            message: 'Lỗi xác thực dữ liệu',
            errors: error.details.map(err => err.message)
        });
    }
};