//Joi là một thư viện JavaScript được sử dụng để xác thực dữ liệu (data validation) trong các ứng dụng Node.js. 
//Nó cho phép bạn định nghĩa các schema (lược đồ) để kiểm tra tính hợp lệ của dữ liệu đầu vào, 
//đảm bảo rằng dữ liệu tuân theo các quy tắc cụ thể trước khi xử lý. Joi rất phổ biến trong các ứng dụng Express 
//để xác thực dữ liệu từ request (như body, query, params) trước khi gửi đến controller hoặc model.

// Các đặc điểm chính của Joi:
// Cú pháp rõ ràng: Dễ dàng định nghĩa các quy tắc xác thực bằng cách sử dụng các phương thức như 
//.string(), .number(), .required(), .min(), .max(), v.v.
// Hỗ trợ nhiều kiểu dữ liệu: Xác thực các kiểu dữ liệu như chuỗi, số, mảng, đối tượng, ngày tháng, v.v.
// Tùy chỉnh thông báo lỗi: Có thể định nghĩa thông báo lỗi cụ thể cho từng quy tắc.
// Xác thực phức tạp: Hỗ trợ các quy tắc phức tạp như kiểm tra điều kiện (when), tham chiếu đến các trường khác 
//(ref), và các biểu thức chính quy.
// Tích hợp dễ dàng: Thường được sử dụng làm middleware trong Express để kiểm tra dữ liệu trước khi xử lý.
const Joi = require('joi');

// Validation schema for creating a task
const createTaskSchema = Joi.object({
    title: Joi.string().min(3).max(255).required().messages({
        'string.base': 'Tiêu đề phải là một chuỗi ký tự',
        'string.min': 'Tiêu đề phải có ít nhất 3 ký tự',
        'string.max': 'Tiêu đề không được vượt quá 255 ký tự',
        'any.required': 'Tiêu đề là bắt buộc'
    }),
    description: Joi.string().max(1000).allow(null, '').messages({
        'string.base': 'Mô tả phải là một chuỗi ký tự',
        'string.max': 'Mô tả không được vượt quá 1000 ký tự'
    }),
    start_date: Joi.date().iso().allow(null).messages({
        'date.base': 'Ngày bắt đầu phải là một ngày hợp lệ (định dạng ISO)',
        'date.format': 'Ngày bắt đầu phải theo định dạng ISO'
    }),
    deadline: Joi.date().iso().greater(Joi.ref('start_date')).allow(null).messages({
        'date.base': 'Hạn chót phải là một ngày hợp lệ (định dạng ISO)',
        'date.greater': 'Hạn chót phải sau ngày bắt đầu',
        'date.format': 'Hạn chót phải theo định dạng ISO'
    }),
    assignee_id: Joi.number().integer().positive().allow(null).messages({
        'number.base': 'ID người được giao phải là một số',
        'number.integer': 'ID người được giao phải là số nguyên',
        'number.positive': 'ID người được giao phải là số dương'
    }),
    priority: Joi.string().valid('low', 'medium', 'high', 'urgent').default('medium').messages({
        'any.only': 'Độ ưu tiên phải là một trong: low, medium, high, urgent',
        'string.base': 'Độ ưu tiên phải là một chuỗi ký tự'
    }),
    status: Joi.string().valid('pending', 'in_progress', 'review', 'completed', 'cancelled').default('pending').messages({
        'any.only': 'Trạng thái phải là một trong: pending, in_progress, review, completed, cancelled',
        'string.base': 'Trạng thái phải là một chuỗi ký tự'
    }),
    progress: Joi.number().integer().min(0).max(100).default(0).messages({
        'number.base': 'Tiến độ phải là một số',
        'number.integer': 'Tiến độ phải là số nguyên',
        'number.min': 'Tiến độ không được nhỏ hơn 0',
        'number.max': 'Tiến độ không được lớn hơn 100'
    }),
    estimated_hours: Joi.number().positive().allow(null).messages({
        'number.base': 'Số giờ ước tính phải là một số',
        'number.positive': 'Số giờ ước tính phải là số dương'
    })
});

// Validation schema for updating a task
const updateTaskSchema = Joi.object({
    title: Joi.string().min(3).max(255).messages({
        'string.base': 'Tiêu đề phải là một chuỗi ký tự',
        'string.min': 'Tiêu đề phải có ít nhất 3 ký tự',
        'string.max': 'Tiêu đề không được vượt quá 255 ký tự'
    }),
    description: Joi.string().max(1000).allow(null, '').messages({
        'string.base': 'Mô tả phải là một chuỗi ký tự',
        'string.max': 'Mô tả không được vượt quá 1000 ký tự'
    }),
    start_date: Joi.date().iso().allow(null).messages({
        'date.base': 'Ngày bắt đầu phải là một ngày hợp lệ (định dạng ISO)',
        'date.format': 'Ngày bắt đầu phải theo định dạng ISO'
    }),
    deadline: Joi.date().iso().greater(Joi.ref('start_date')).allow(null).messages({
        'date.base': 'Hạn chót phải là một ngày hợp lệ (định dạng ISO)',
        'date.greater': 'Hạn chót phải sau ngày bắt đầu',
        'date.format': 'Hạn chót phải theo định dạng ISO'
    }),
    assignee_id: Joi.number().integer().positive().allow(null).messages({
        'number.base': 'ID người được giao phải là một số',
        'number.integer': 'ID người được giao phải là số nguyên',
        'number.positive': 'ID người được giao phải là số dương'
    }),
    priority: Joi.string().valid('low', 'medium', 'high', 'urgent').messages({
        'any.only': 'Độ ưu tiên phải là một trong: low, medium, high, urgent',
        'string.base': 'Độ ưu tiên phải là một chuỗi ký tự'
    }),
    status: Joi.string().valid('pending', 'in_progress', 'review', 'completed', 'cancelled').messages({
        'any.only': 'Trạng thái phải là một trong: pending, in_progress, review, completed, cancelled',
        'string.base': 'Trạng thái phải là một chuỗi ký tự'
    }),
    progress: Joi.number().integer().min(0).max(100).messages({
        'number.base': 'Tiến độ phải là một số',
        'number.integer': 'Tiến độ phải là số nguyên',
        'number.min': 'Tiến độ không được nhỏ hơn 0',
        'number.max': 'Tiến độ không được lớn hơn 100'
    }),
    estimated_hours: Joi.number().positive().allow(null).messages({
        'number.base': 'Số giờ ước tính phải là một số',
        'number.positive': 'Số giờ ước tính phải là số dương'
    })
}).min(1).messages({
    'object.min': 'Phải cung cấp ít nhất một trường để cập nhật'
});

// Middleware for creating a task
exports.createTaskValidator = async (req, res, next) => {
    try {
        await createTaskSchema.validateAsync(req.body, { abortEarly: false });
        next();
    } catch (error) {
        return res.status(400).json({
            message: 'Lỗi xác thực dữ liệu',
            errors: error.details.map(err => err.message)
        });
    }
};

// Middleware for updating a task
exports.updateTaskValidator = async (req, res, next) => {
    try {
        await updateTaskSchema.validateAsync(req.body, { abortEarly: false });
        next();
    } catch (error) {
        return res.status(400).json({
            message: 'Lỗi xác thực dữ liệu',
            errors: error.details.map(err => err.message)
        });
    }
};