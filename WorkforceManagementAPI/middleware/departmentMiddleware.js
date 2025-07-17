exports.validateDepartmentData = async (req, res, next) => {
    const { name, code, description, manager_id } = req.body;

    // Validation for required fields
    if (!name || !code) {
        return res.status(400).json({
            success: false,
            message: 'Name and code are required'
        });
    }

    // Validate name length
    if (name.length < 2 || name.length > 255) {
        return res.status(400).json({
            success: false,
            message: 'Name must be between 2 and 255 characters'
        });
    }

    // Validate code format (alphanumeric, 2-50 characters)
    const codeRegex = /^[a-zA-Z0-9]{2,50}$/;
    if (!codeRegex.test(code)) {
        return res.status(400).json({
            success: false,
            message: 'Code must be alphanumeric and between 2 and 50 characters'
        });
    }

    // Check if code is unique (for create or update)
    try {
        const existingDepartment = await require('../models/departmentModel').findByCode(code);
        if (existingDepartment && existingDepartment.id != req.params?.id) {
            return res.status(400).json({
                success: false,
                message: 'Department not found!'
            });
        }
    } catch (error) {
        console.error('Error checking department code:', error);
        return res.status(500).json({
            success: false,
            message: 'Server error during validation'
        });
    }

    // Validate manager_id if provided
    if (manager_id) {
        try {
            const [rows] = await require('../config/db').execute(
                'SELECT id FROM employees WHERE id = ?',
                [manager_id]
            );
            if (!rows.length) {
                return res.status(400).json({
                    success: false,
                    message: 'Invalid manager_id: Employee not found'
                });
            }
        } catch (error) {
            console.error('Error validating manager_id:', error);
            return res.status(500).json({
                success: false,
                message: 'Server error during manager validation'
            });
        }
    }

    next();
};