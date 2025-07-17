// employeeMiddleware.js
exports.validateEmployeeData = async (req, res, next) => {
    const isUpdate = req.method === 'PUT'; // Check if this is an update request
    const { user_id, employee_code, first_name, last_name, email, phone, position, hire_date, department_id } = req.body;

    if (!isUpdate) {
        // Validation for create (POST) - all fields required
        if (!user_id || !employee_code || !first_name || !last_name || !email || !phone || !position || !hire_date) {
            return res.status(400).json({ success: false, message: 'All fields are required for creating an employee' });
        }
    } else {
        // Validation for update (PUT) - at least one field must be provided
        if (!first_name && !last_name && !email && !phone && department_id === undefined && !position) {
            return res.status(400).json({ success: false, message: 'At least one field must be provided for update' });
        }
    }

    // Validate email if provided
    if (email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            return res.status(400).json({ success: false, message: 'Invalid email format' });
        }
        // Check if email is unique (excluding the current employee for updates)
        try {
            const [rows] = await require('../config/db').execute(
                'SELECT id FROM employees WHERE email = ?' + (isUpdate ? ' AND id != ?' : ''),
                isUpdate ? [email, req.params.id] : [email]
            );
            if (rows.length > 0) {
                return res.status(400).json({ success: false, message: 'Email already in use' });
            }
        } catch (error) {
            console.error('Error checking email uniqueness:', error);
            return res.status(500).json({ success: false, message: 'Server error during validation' });
        }
    }

    // Validate phone if provided
    if (phone) {
        const phoneRegex = /^\d{10}$/;
        if (!phoneRegex.test(phone)) {
            return res.status(400).json({ success: false, message: 'Invalid phone number format' });
        }
    }

    // Validate department_id if provided and not null
    if (department_id !== undefined && department_id !== null) {
        try {
            const [rows] = await require('../config/db').execute(
                'SELECT id FROM departments WHERE id = ?',
                [department_id]
            );
            if (!rows.length) {
                return res.status(400).json({ success: false, message: 'Invalid department_id: Department not found' });
            }
        } catch (error) {
            console.error('Error validating department_id:', error);
            return res.status(500).json({ success: false, message: 'Server error during validation' });
        }
    }

    next();
};