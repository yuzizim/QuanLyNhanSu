// employeeController.js
const Employee = require('../models/employeeModel');

// Create a new employee
exports.createEmployee = async (req, res) => {
    try {
        const { user_id, employee_code, first_name, last_name, email, phone, department_id, position, hire_date } = req.body;

        // Validation for required fields
        if (!user_id || !employee_code || !first_name || !last_name || !email || !phone || !position || !hire_date) {
            return res.status(400).json({ success: false, message: 'All fields are required' });
        }

        // Create employee in database
        const employee = await Employee.create({
            user_id,
            employee_code,
            first_name,
            last_name,
            email,
            phone,
            department_id,
            position,
            hire_date
        });

        return res.status(201).json({
            success: true,
            message: 'Employee successfully created',
            data: employee
        });
    } catch (error) {
        console.error('Error in createEmployee:', error);
        return res.status(500).json({ success: false, message: 'Server error', error: error.message });
    }
};

// Get all employees
exports.getAllEmployees = async (req, res) => {
    try {
        const { page, limit, search, filter } = req.query;
        const employeesData = await Employee.getAll({
            page: parseInt(page) || 1,
            limit: parseInt(limit) || 10,
            search: search || '',
            filter: filter || ''
        });
        return res.status(200).json({
            success: true,
            data: {
                employees: employeesData.employees,
                total: employeesData.total,
                page: employeesData.page,
                limit: employeesData.limit
            },
            message: 'Employees fetched successfully'
        });
    } catch (error) {
        console.error('Error fetching employees:', error);
        return res.status(500).json({
            success: false,
            message: error.message || 'Internal server error'
        });
    }
};

// Get employee by ID
exports.getEmployeeById = async (req, res) => {
    try {
        const employeeId = req.params.id;
        const employee = await Employee.findById(employeeId);

        if (!employee) {
            return res.status(404).json({ success: false, message: 'Employee not found' });
        }

        return res.status(200).json({
            success: true,
            data: employee
        });
    } catch (error) {
        console.error('Error in getEmployeeById:', error);
        return res.status(500).json({ success: false, message: 'Server error', error: error.message });
    }
};

// Update employee
exports.updateEmployee = async (req, res) => {
    try {
        const employeeId = req.params.id;
        const { first_name, last_name, email, phone, department_id, position } = req.body;

        // Check if employee exists
        const employee = await Employee.findById(employeeId);
        if (!employee) {
            return res.status(404).json({ success: false, message: 'Employee not found' });
        }

        // Prepare update data (only include provided fields)
        const updateData = {};
        if (first_name) updateData.first_name = first_name;
        if (last_name) updateData.last_name = last_name;
        if (email) updateData.email = email;
        if (phone) updateData.phone = phone;
        if (department_id !== undefined) updateData.department_id = department_id; // Allow null
        if (position) updateData.position = position;

        // Check if any fields were provided
        if (Object.keys(updateData).length === 0) {
            return res.status(400).json({ success: false, message: 'No fields provided for update' });
        }

        // Update employee
        const updatedEmployee = await Employee.update(employeeId, updateData);

        if (!updatedEmployee) {
            return res.status(400).json({ success: false, message: 'Failed to update employee' });
        }

        return res.status(200).json({
            success: true,
            message: 'Employee updated successfully',
            data: updatedEmployee
        });
    } catch (error) {
        console.error('Error in updateEmployee:', error);
        return res.status(500).json({ success: false, message: 'Server error', error: error.message });
    }
};

// Delete employee
exports.deleteEmployee = async (req, res) => {
    try {
        const employeeId = req.params.id;

        // Check if employee exists
        const employee = await Employee.findById(employeeId);
        if (!employee) {
            return res.status(404).json({ success: false, message: 'Employee not found' });
        }

        // Delete employee
        await Employee.delete(employeeId);

        return res.status(200).json({
            success: true,
            message: 'Employee deleted successfully'
        });
    } catch (error) {
        console.error('Error in deleteEmployee:', error);
        return res.status(500).json({ success: false, message: 'Server error', error: error.message });
    }
};