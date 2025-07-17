// controllers/userController.js - User controller
const User = require('../models/userModel');
const Employee = require('../models/employeeModel');
const { generateToken } = require('../utils/jwt');
const nodemailer = require('nodemailer');
const moment = require('moment-timezone');
const { OAuth2Client } = require('google-auth-library');
const client = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);

const validatePassword = (password) => {
    if (password.length < 8) {
        return "Password must be at least 8 characters long.";
    }
    if (!/[A-Z]/.test(password)) {
        return "Password must contain at least one uppercase letter.";
    }
    if (!/[a-z]/.test(password)) {
        return "Password must contain at least one lowercase letter.";
    }
    if (!/[0-9]/.test(password)) {
        return "Password must contain at least one number.";
    }
    if (!/[@$!%*?&]/.test(password)) {
        return "Password must contain at least one special character (@$!%*?&).";
    }
    return null;
};

exports.registerUser = async (req, res) => {
    try {
        const { username, email, password, role } = req.body;

        if (!username || !email || !password) {
            return res.status(400).json({
                success: false,
                message: 'Username, email, and password are required'
            });
        }

        const existingUsername = await User.findByUsername(username);
        if (existingUsername) {
            return res.status(400).json({
                success: false,
                message: 'Username already in use'
            });
        }

        const emailExists = await User.findByEmail(email);
        if (emailExists) {
            return res.status(400).json({
                success: false,
                message: 'Email already in use'
            });
        }

        const user = await User.create({
            username,
            email,
            password,
            role: role || 'employee'
        });

        const userResponse = { ...user };
        delete userResponse.password_hash;

        return res.status(201).json({
            success: true,
            message: 'User successfully registered',
            data: userResponse
        });
    } catch (error) {
        console.error('Error in registerUser:', error);
        return res.status(500).json({
            success: false,
            message: error.message === 'Failed to find user' ? 'Database query failed' : 'Server error',
            error: error.message
        });
    }
};

// Other methods (loginUser, getUserById, updateUser, getCurrentUser) remain unchanged
exports.loginUser = async (req, res) => {
    try {
        // console.log('Request headers:', req.headers);
        // console.log('Request body:', req.body);
        if (!req.body) {
            return res.status(400).json({
                success: false,
                message: 'Request body is missing or invalid'
            });
        }
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({
                success: false,
                message: 'Email and password are required'
            });
        }

        const user = await User.login(email, password);

        if (!user) {
            return res.status(401).json({
                success: false,
                message: 'Invalid credentials'
            });
        }

        // if (!user.is_active) {
        //     return res.status(401).json({
        //         success: false,
        //         message: 'Account is not activated'
        //     });
        // }

        const token = generateToken(user.id);
        const userResponse = { ...user };
        delete userResponse.password_hash;

        return res.status(200).json({
            success: true,
            message: 'Login successful',
            token,
            data: userResponse
        });
    } catch (error) {
        console.error('Error in loginUser:', error);
        return res.status(500).json({
            success: false,
            message: 'Server error',
            error: error.message
        });
    }
};

// exports.googleLogin = async (req, res) => {
//     const { idToken } = req.body;
//     try {
//         const ticket = await client.verifyIdToken({
//             idToken,
//             audience: process.env.GOOGLE_CLIENT_ID,
//         });

//         const payload = ticket.getPayload();
//         const { email, name } = payload;

//         // Check if user exists
//         let user = await User.findByEmail(email);

//         if (!user) {
//             return res.status(404).json({
//                 success: false,
//                 message: 'User not found'
//             });
//         }

//         const token = generateToken(user.id);
//         const userResponse = { ...user };
//         delete userResponse.password_hash;

//         return res.status(200).json({
//             success: true,
//             message: 'Google login successful',
//             token,
//             data: userResponse
//         });

//     } catch (error) {
//         console.error('Error during Google login:', error);
//         return res.status(401).json({ success: false, error: 'Invalid ID token' });
//     }
// };


exports.googleLogin = async (req, res) => {
    const { idToken } = req.body;
    try {
        console.log('Received idToken:', idToken);
        const ticket = await client.verifyIdToken({
            idToken,
            audience: process.env.GOOGLE_CLIENT_ID,
        });
        const payload = ticket.getPayload();
        console.log('Google payload:', payload);
        const { email, name } = payload;

        let user = await User.findByEmail(email);
        console.log('User found:', user);

        if (!user) {
            console.log('User not found for email:', email);
            return res.status(404).json({
                success: false,
                message: 'This email is not registered. Please sign up.'
            });
        }

        const token = generateToken(user.id);
        const userResponse = { ...user };
        delete userResponse.password_hash;

        return res.status(200).json({
            success: true,
            message: 'Google login successful',
            token,
            data: userResponse
        });
    } catch (error) {
        console.error('Error during Google login:', error.message, error.stack);
        return res.status(401).json({ success: false, error: 'Invalid ID token' });
    }
};

exports.getUserById = async (req, res) => {
    try {
        const userId = req.params.id;
        const user = await User.findById(userId);

        if (!user) {
            return res.status(404).json({
                success: false,
                message: 'User not found'
            });
        }

        const userResponse = { ...user };
        delete userResponse.password_hash;

        return res.status(200).json({
            success: true,
            data: userResponse
        });
    } catch (error) {
        console.error('Error in getUserById:', error);
        return res.status(500).json({
            success: false,
            message: 'Server error',
            error: error.message
        });
    }
};

exports.updateUser = async (req, res) => {
    try {
        const { id } = req.params; const userData = req.body;

        const updatedUser = await User.update(id, userData);
        res.status(200).json({
            success: true,
            data: updatedUser,
            message: 'User updated successfully'
        });
    } catch (error) {
        if (error.message === 'User not found') {
            return res.status(404).json({
                success: false,
                message: 'User not found'
            });
        }
        res.status(500).json({
            success: false,
            message: 'Error updating user',
            error: error.message
        });
    }
};

exports.getCurrentUser = async (req, res) => {
    try {
        const userResponse = { ...req.user };
        delete userResponse.password_hash;

        return res.status(200).json({
            success: true,
            data: userResponse
        });
    } catch (error) {
        console.error('Error in getCurrentUser:', error);
        return res.status(500).json({
            success: false,
            message: 'Server error',
            error: error.message
        });
    }
};



exports.requestPasswordReset = async (req, res) => {
    const { email } = req.body;
    const user = await User.findByEmail(email);
    if (!user) return res.status(404).json({ message: 'User not found' });

    const otp = Math.floor(100000 + Math.random() * 900000).toString();
    const expiresAt = moment().tz('Asia/Ho_Chi_Minh').add(10, 'minutes').format('YYYY-MM-DD HH:mm:ss');

    await User.saveOTP(email, otp, expiresAt);

    const transporter = nodemailer.createTransport({
        service: 'Gmail',
        auth: {
            user: process.env.EMAIL_USER,
            pass: process.env.EMAIL_PASS
        }
    });

    const mailOptions = {
        from: `Your App <${process.env.EMAIL_USER}>`,
        to: email,
        subject: '🔐 OTP for Password Reset',
        html: `
            <div style="font-family: Arial, sans-serif; padding: 20px;">
                <h2 style="color: #0d6efd;">Password Reset Request</h2>
                <p>Hello <strong>${user.username || ''}</strong>,</p>
                <p>Your One-Time Password (OTP) for resetting your password is:</p>
                <div style="font-size: 24px; font-weight: bold; margin: 10px 0;">${otp}</div>
                <p>This OTP is valid for <strong>10 minutes</strong>.</p>
                <p>If you did not request this, please ignore this email.</p>
                <hr>
                <p style="font-size: 12px; color: #888;">Sent at: ${moment().tz('Asia/Ho_Chi_Minh').format('YYYY-MM-DD HH:mm:ss')}</p>
            </div>
        `
    };

    await transporter.sendMail(mailOptions);
    console.log(`[OTP] Sent to ${email} at ${expiresAt}`);
    res.json({ message: 'OTP sent to email' });
}

exports.resetPassword = async (req, res) => {
    const { email, otp, newPassword } = req.body;

    const validationError = validatePassword(newPassword);
    if (validationError) return res.status(400).json({ message: validationError });

    const user = await User.verifyOTP(email, otp);
    if (!user) return res.status(400).json({ message: 'Invalid or expired OTP' });

    await User.updatePassword(email, newPassword);
    res.json({ message: 'Password updated successfully' });
};


exports.getProfile = async (req, res) => {
    try {
        const userId = req.user.id;
        const userRole = req.user.role;

        let profile;

        if (userRole === 'admin') {
            // Admin profile: chỉ username, email, role
            profile = await User.getAdminProfile(userId);

            if (!profile) {
                return res.status(404).json({
                    success: false,
                    message: 'Admin profile not found'
                });
            }

            return res.status(200).json({
                success: true,
                data: {
                    id: profile.id,
                    username: profile.username,
                    email: profile.email,
                    role: profile.role,
                    profile_type: 'admin'
                }
            });

        } else {
            // HR, Employee, Dep_Manager profile: thông tin đầy đủ
            profile = await User.getEmployeeProfile(userId);

            if (!profile) {
                return res.status(404).json({
                    success: false,
                    message: 'Employee profile not found'
                });
            }

            return res.status(200).json({
                success: true,
                data: {
                    id: profile.user_id,
                    username: profile.username,
                    email: profile.employee_email || profile.user_email,
                    role: profile.role,
                    employee_code: profile.employee_code,
                    full_name: `${profile.first_name} ${profile.last_name}`,
                    first_name: profile.first_name,
                    last_name: profile.last_name,
                    phone: profile.phone,
                    position: profile.position,
                    department: {
                        name: profile.department_name,
                        code: profile.department_code
                    },
                    hire_date: profile.hire_date,
                    status: profile.employee_status,
                    profile_type: 'employee'
                }
            });
        }

    } catch (error) {
        console.error('Get profile error:', error);
        return res.status(500).json({
            success: false,
            message: 'Internal server error'
        });
    }
}


exports.updateProfile = async (req, res) => {
    try {
        const userId = req.user.id;
        const userRole = req.user.role;
        const updateData = req.body;

        // Fetch current user data
        const currentUser = await User.findById(userId);
        if (!currentUser) {
            return res.status(404).json({
                success: false,
                message: 'User not found'
            });
        }

        if (userRole === 'admin') {
            const { username, email } = updateData;

            // Check if any changes were made
            const hasChanges = (username && username !== currentUser.username) ||
                (email && email !== currentUser.email);
            if (!hasChanges) {
                return res.status(400).json({
                    success: false,
                    message: 'No changes provided for update'
                });
            }

            // Validate username if provided and different
            if (username && username !== currentUser.username) {
                const existingUsername = await User.findByUsername(username);
                if (existingUsername && existingUsername.id !== userId) {
                    return res.status(400).json({
                        success: false,
                        message: 'Username is already taken'
                    });
                }
            }

            // Validate email if provided and different
            if (email && email !== currentUser.email) {
                if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                    return res.status(400).json({
                        success: false,
                        message: 'Invalid email format'
                    });
                }
                const existingEmail = await User.findByEmail(email);
                if (existingEmail && existingEmail.id !== userId) {
                    return res.status(400).json({
                        success: false,
                        message: 'Email is already taken'
                    });
                }
            }

            const updated = await User.updateUser(userId, { username, email });

            if (!updated) {
                return res.status(400).json({
                    success: false,
                    message: 'Failed to update admin profile'
                });
            }

            return res.status(200).json({
                success: true,
                message: 'Admin profile updated successfully'
            });

        } else {
            const { email, first_name, last_name, phone, position } = updateData;

            // Validate employee from userId
            const employee = await Employee.findByUserId(userId);
            console.log('Employee found:', employee);
            if (!employee) {
                return res.status(404).json({
                    success: false,
                    message: 'Employee not found'
                });
            }

            // Check if any changes were made
            const hasChanges = (email && email !== currentUser.email) ||
                (first_name && first_name !== employee.first_name) ||
                (last_name && last_name !== employee.last_name) ||
                (phone && phone !== employee.phone) ||
                (position && position !== employee.position);
            if (!hasChanges) {
                return res.status(400).json({
                    success: false,
                    message: 'No changes provided for update'
                });
            }

            // Validate email if provided and different
            if (email && email !== currentUser.email) {
                if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                    return res.status(400).json({
                        success: false,
                        message: 'Invalid email format'
                    });
                }
                const existingEmail = await User.findByEmail(email);
                if (existingEmail && existingEmail.id !== userId) {
                    return res.status(400).json({
                        success: false,
                        message: 'Email is already taken'
                    });
                }
            }

            // Update user email if provided and different
            if (email && email !== currentUser.email) {
                await User.updateUser(userId, { email });
            }

            // Update employee details if any fields are provided
            if (hasChanges) {
                const updatedEmployee = await Employee.update(employee.id, {
                    email: email || null,
                    first_name: first_name || null,
                    last_name: last_name || null,
                    phone: phone || null,
                    position: position || null
                });
                if (!updatedEmployee) {
                    return res.status(400).json({
                        success: false,
                        message: 'Failed to update employee profile'
                    });
                }
            }

            return res.status(200).json({
                success: true,
                message: 'Profile updated successfully'
            });
        }

    } catch (error) {
        console.error('Update profile error:', error);
        return res.status(500).json({
            success: false,
            message: 'Internal server error'
        });
    }
};

// Lấy profile của user khác (chỉ admin và hr có quyền)
exports.getUserProfile = async (req, res) => {
    try {
        const { userId } = req.params;
        const currentUserRole = req.user.role;

        // Chỉ admin và hr có quyền xem profile của user khác
        if (!['admin', 'hr'].includes(currentUserRole)) {
            return res.status(403).json({
                success: false,
                message: 'Access denied'
            });
        }

        // Lấy thông tin user cần xem
        const targetUser = await User.findById(userId);

        if (!targetUser) {
            return res.status(404).json({
                success: false,
                message: 'User not found'
            });
        }

        let profile;

        if (targetUser.role === 'admin') {
            profile = await User.getAdminProfile(userId);

            return res.status(200).json({
                success: true,
                data: {
                    id: profile.id,
                    username: profile.username,
                    email: profile.email,
                    role: profile.role,
                    profile_type: 'admin'
                }
            });

        } else {
            profile = await User.getEmployeeProfile(userId);

            return res.status(200).json({
                success: true,
                data: {
                    id: profile.user_id,
                    username: profile.username,
                    email: profile.employee_email || profile.user_email,
                    role: profile.role,
                    employee_code: profile.employee_code,
                    full_name: `${profile.first_name} ${profile.last_name}`,
                    first_name: profile.first_name,
                    last_name: profile.last_name,
                    phone: profile.phone,
                    position: profile.position,
                    department: {
                        name: profile.department_name,
                        code: profile.department_code
                    },
                    hire_date: profile.hire_date,
                    status: profile.employee_status,
                    profile_type: 'employee'
                }
            });
        }

    } catch (error) {
        console.error('Get user profile error:', error);
        return res.status(500).json({
            success: false,
            message: 'Internal server error'
        });
    }
}


exports.createUser = async (req, res) => {
    try {
        const { username, email, password, role } = req.body;

        if (!username || !email || !password || !role) {
            return res.status(400).json({
                success: false,
                message: 'All fields are required'
            });
        }

        const user = await User.createUser(username, email, password, role);

        return res.status(201).json({
            success: true,
            message: 'User created successfully',
            data: user
        });

    } catch (error) {
        console.error('Error in createUser:', error);
        return res.status(500).json({
            success: false,
            message: 'Internal server error'
        });
    }
}

exports.getUsers = async (req, res) => {
    try {

        const { search = '', filter = 'all', page = 1, limit = 10 } = req.query;
        const data = await User.getAllUsers(search, filter, parseInt(page), parseInt(limit));

        return res.status(200).json({
            success: true,
            data: {
                users: data.users,
                stats: data.stats
            }
        });
    } catch (error) {
        console.error('Error in getUsers:', error);
        return res.status(500).json({
            success: false,
            message: 'Server error',
            error: error.message
        });
    }
};


exports.deleteUser = async (req, res) => {
    try {
        const { id } = req.params;
        await User.deleteUser(id);

        return res.status(200).json({
            success: true,
            message: 'User deactivated successfully'
        });
    } catch (error) {
        console.error('Error in deleteUser:', error);
        return res.status(500).json({
            success: false,
            message: 'Server error',
            error: error.message
        });
    }
};
