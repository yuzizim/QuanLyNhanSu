const Stats = require('../models/statsModel');

exports.getDashboardStats = async (req, res) => {
    try {
        // Only allow admin access
        if (req.user.role !== 'admin') {
            return res.status(403).json({
                success: false,
                message: 'Access denied. Admin only.'
            });
        }

        const stats = await Stats.getDashboardStats();
        return res.status(200).json({
            success: true,
            data: stats
        });
    } catch (error) {
        console.error('Error in getDashboardStats:', error);
        return res.status(500).json({
            success: false,
            message: 'Server error',
            error: error.message
        });
    }
};