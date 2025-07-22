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


exports.getAllEmployees = async (req, res) => {
    try {
        const { page, limit, search, filter } = req.query;
        let employeesData;

        if (req.user.role === 'dep_manager') {
            let departmentId = req.user.department_id;
            if (!departmentId) {
                const managerEmployee = await require('../models/employeeModel').findByUserId(req.user.id);
                departmentId = managerEmployee ? managerEmployee.department_id : null;
            }
            if (!departmentId) {
                return res.status(400).json({
                    success: false,
                    message: 'Department not found for this dep_manager'
                });
            }
            employeesData = await require('../models/employeeModel').getAll({
                page: parseInt(page) || 1,
                limit: parseInt(limit) || 10,
                search: search || '',
                filter: filter || '',
                department_id: departmentId
            });
        } else {
            employeesData = await require('../models/employeeModel').getAll({
                page: parseInt(page) || 1,
                limit: parseInt(limit) || 10,
                search: search || '',
                filter: filter || ''
            });
        }

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

exports.getAllEmployees = async (req, res) => {
    try {
        const { page, limit, search, filter } = req.query;
        let employeesData;

        // Nếu là dep_manager thì chỉ lấy nhân viên thuộc phòng ban mình quản lý
        if (req.user.role === 'dep_manager') {
            // Lấy employee record để biết department_id của dep_manager
            // Nếu đã gắn department_id vào req.user thì lấy luôn, nếu chưa thì phải truy vấn thêm
            let departmentId = req.user.department_id;
            // Nếu chưa có thì truy DB từ employeeModel
            if (!departmentId) {
                const Employee = require('../models/employeeModel');
                const managerEmployee = await Employee.findByUserId(req.user.id);
                departmentId = managerEmployee ? managerEmployee.department_id : null;
            }
            if (!departmentId) {
                return res.status(400).json({
                    success: false,
                    message: 'Department not found for this dep_manager'
                });
            }
            // Lấy danh sách nhân viên theo department_id
            employeesData = await require('../models/employeeModel').getAll({
                page: parseInt(page) || 1,
                limit: parseInt(limit) || 10,
                search: search || '',
                filter: filter || '',
                department_id: departmentId // <-- thêm tham số này vào model
            });
        } else {
            // admin, hr: lấy tất cả
            employeesData = await require('../models/employeeModel').getAll({
                page: parseInt(page) || 1,
                limit: parseInt(limit) || 10,
                search: search || '',
                filter: filter || ''
            });
        }

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