// // controllers/taskController.js
// const Task = require('../models/taskModel');
// const Employee = require('../models/employeeModel');

// // Check if employee exists
// const checkEmployeeExistence = async (employeeId) => {
//     const employee = await Employee.findById(employeeId);
//     return !!employee;
// };

// // Create a new task
// exports.createTask = async (req, res) => {
//     try {
//         const { assignee_id, creator_id } = req.body;

//         // Validate employee existence
//         if (assignee_id) {
//             const assigneeExists = await checkEmployeeExistence(assignee_id);
//             if (!assigneeExists) {
//                 return res.status(400).json({ message: 'Assignee not found' });
//             }
//         }

//         const creatorExists = await checkEmployeeExistence(creator_id);
//         if (!creatorExists) {
//             return res.status(400).json({ message: 'Creator not found' });
//         }

//         const newTask = await Task.create(req.body);
//         if (newTask) {
//             return res.status(201).json({ message: 'Task created successfully', task: newTask });
//         }
//         return res.status(400).json({ message: 'Task creation failed' });
//     } catch (error) {
//         console.error('Error creating task:', error);
//         return res.status(500).json({ message: error.message || 'Internal server error' });
//     }
// };

// // Get all tasks
// exports.getAllTasks = async (req, res) => {
//     try {
//         const tasks = await Task.getAll();
//         return res.status(200).json({ tasks });
//     } catch (error) {
//         console.error('Error fetching tasks:', error);
//         return res.status(500).json({ message: error.message || 'Internal server error' });
//     }
// };

// // Get task by ID
// exports.getTaskById = async (req, res) => {
//     try {
//         const { id } = req.params;
//         const task = await Task.findById(id);
//         if (task) {
//             return res.status(200).json({ task });
//         }
//         return res.status(404).json({ message: 'Task not found' });
//     } catch (error) {
//         console.error('Error fetching task:', error);
//         return res.status(500).json({ message: error.message || 'Internal server error' });
//     }
// };

// // Update a task
// exports.updateTask = async (req, res) => {
//     try {
//         const { id } = req.params;
//         const { assignee_id, creator_id } = req.body;

//         // Validate employee existence
//         if (assignee_id) {
//             const assigneeExists = await checkEmployeeExistence(assignee_id);
//             if (!assigneeExists) {
//                 return res.status(400).json({ message: 'Assignee not found' });
//             }
//         }
//         if (creator_id) {
//             const creatorExists = await checkEmployeeExistence(creator_id);
//             if (!creatorExists) {
//                 return res.status(400).json({ message: 'Creator not found' });
//             }
//         }

//         const isUpdated = await Task.update(id, req.body);
//         if (isUpdated) {
//             const updatedTask = await Task.findById(id);
//             return res.status(200).json({ message: 'Task updated successfully', task: updatedTask });
//         }
//         return res.status(404).json({ message: 'Task not found or no changes detected' });
//     } catch (error) {
//         console.error('Error updating task:', error);
//         return res.status(500).json({ message: error.message || 'Internal server error' });
//     }
// };

// // Delete a task
// exports.deleteTask = async (req, res) => {
//     try {
//         const { id } = req.params;
//         const isDeleted = await Task.delete(id);
//         if (isDeleted) {
//             return res.status(200).json({ message: 'Task deleted successfully' });
//         }
//         return res.status(404).json({ message: 'Task not found' });
//     } catch (error) {
//         console.error('Error deleting task:', error);
//         return res.status(500).json({ message: error.message || 'Internal server error' });
//     }
// };

// // Get task progress report
// exports.getTaskReport = async (req, res) => {
//     try {
//         const report = await Task.getReport();
//         return res.status(200).json({ report });
//     } catch (error) {
//         console.error('Error generating task report:', error);
//         return res.status(500).json({ message: error.message || 'Internal server error' });
//     }
// };


// controllers/taskController.js
const Task = require('../models/taskModel');
const Employee = require('../models/employeeModel');

// Check if employee exists
const checkEmployeeExistence = async (employeeId) => {
    const employee = await Employee.findById(employeeId);
    return !!employee;
};

// Create a new task
exports.createTask = async (req, res) => {
    try {
        const { assignee_id, creator_id } = req.body;

        // Validate employee existence
        if (assignee_id) {
            const assigneeExists = await checkEmployeeExistence(assignee_id);
            if (!assigneeExists) {
                return res.status(400).json({ success: false, message: 'Assignee not found' });
            }
        }

        const newTask = await Task.create(req.body);
        if (newTask) {
            return res.status(201).json({ success: true, message: 'Task created successfully', task: newTask });
        }
        return res.status(400).json({ success: false, message: 'Task creation failed' });
    } catch (error) {
        console.error('Error creating task:', error);
        return res.status(500).json({ success: false, message: error.message || 'Internal server error' });
    }
};

// Get all tasks
exports.getAllTasks = async (req, res) => {
    try {
        const { page = 1, limit = 10, status, priority, assignee_id, search } = req.query;
        const tasks = await Task.getAll({
            page: parseInt(page),
            limit: parseInt(limit),
            status,
            priority,
            assignee_id: parseInt(assignee_id),
            search
        });
        return res.status(200).json({ success: true, data: tasks });
    } catch (error) {
        console.error('Error fetching tasks:', error);
        return res.status(500).json({ success: false, message: error.message || 'Internal server error' });
    }
};

// Get task by ID
exports.getTaskById = async (req, res) => {
    try {
        const { id } = req.params;
        const task = await Task.findById(id);
        if (task) {
            return res.status(200).json({ success: true, task });
        }
        return res.status(404).json({ success: false, message: 'Task not found' });
    } catch (error) {
        console.error('Error fetching task:', error);
        return res.status(500).json({ success: false, message: error.message || 'Internal server error' });
    }
};

// Update a task
exports.updateTask = async (req, res) => {
    try {
        const { id } = req.params;
        const { assignee_id, creator_id } = req.body;

        // Validate employee existence
        if (assignee_id) {
            const assigneeExists = await checkEmployeeExistence(assignee_id);
            if (!assigneeExists) {
                return res.status(400).json({ success: false, message: 'Assignee not found' });
            }
        }
        // if (creator_id) {
        //     const creatorExists = await checkEmployeeExistence(creator_id);
        //     if (!creatorExists) {
        //         return res.status(400).json({ success: false, message: 'Creator not found' });
        //     }
        // }

        const isUpdated = await Task.update(id, req.body);
        if (isUpdated) {
            const updatedTask = await Task.findById(id);
            return res.status(200).json({ success: true, message: 'Task updated successfully', task: updatedTask });
        }
        return res.status(404).json({ success: false, message: 'Task not found or no changes detected' });
    } catch (error) {
        console.error('Error updating task:', error);
        return res.status(500).json({ success: false, message: error.message || 'Internal server error' });
    }
};

// Delete a task
exports.deleteTask = async (req, res) => {
    try {
        const { id } = req.params;
        const isDeleted = await Task.delete(id);
        if (isDeleted) {
            return res.status(200).json({ success: true, message: 'Task deleted successfully' });
        }
        return res.status(404).json({ success: false, message: 'Task not found' });
    } catch (error) {
        console.error('Error deleting task:', error);
        return res.status(500).json({ success: false, message: error.message || 'Internal server error' });
    }
};

// Get task progress report
exports.getTaskReport = async (req, res) => {
    try {
        const report = await Task.getReport();
        return res.status(200).json({ success: true, report });
    } catch (error) {
        console.error('Error generating task report:', error);
        return res.status(500).json({ success: false, message: error.message || 'Internal server error' });
    }
};