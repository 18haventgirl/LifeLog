package com.lifelog.app.data.model

object PresetData {

    data class PresetCategory(
        val name: String,
        val icon: String,
        val color: String,
        val activities: List<String>
    )

    val categories = listOf(
        PresetCategory("学习", "📚", "#3B82F6", listOf(
            "上课", "做作业", "复习备考", "写论文", "背单词",
            "看网课", "阅读文献", "小组讨论", "查资料", "考试"
        )),
        PresetCategory("工作", "💼", "#8B5CF6", listOf(
            "上班", "开会", "加班", "处理邮件", "项目推进", "写报告", "摸鱼"
        )),
        PresetCategory("运动健身", "🏃", "#10B981", listOf(
            "跑步", "健身房", "球类运动", "瑜伽", "散步",
            "骑车", "游泳", "跳绳", "拉伸"
        )),
        PresetCategory("娱乐", "🎮", "#F59E0B", listOf(
            "刷手机", "打游戏", "看剧/电影", "刷短视频", "逛B站",
            "听音乐", "看小说", "刷社交媒体", "看直播"
        )),
        PresetCategory("饮食", "🍜", "#EF4444", listOf(
            "早餐", "午餐", "晚餐", "夜宵", "做饭", "下午茶", "零食"
        )),
        PresetCategory("睡眠", "😴", "#6366F1", listOf(
            "晚睡", "午休", "赖床", "失眠", "小憩"
        )),
        PresetCategory("生活杂务", "🏠", "#EC4899", listOf(
            "打扫卫生", "洗衣服", "购物/采购", "洗澡洗漱",
            "取快递", "缴费", "整理房间", "倒垃圾", "修东西"
        )),
        PresetCategory("社交通讯", "👥", "#14B8A6", listOf(
            "和朋友聊天", "打电话", "回消息", "社交聚会",
            "约会", "家庭聚会", "视频通话"
        )),
        PresetCategory("交通出行", "🚗", "#F97316", listOf(
            "通勤", "坐地铁/公交", "打车", "自驾",
            "步行", "出差旅途", "等人/等车"
        )),
        PresetCategory("个人成长", "🧘", "#06B6D4", listOf(
            "冥想", "写日记", "复盘反思", "学新技能",
            "看纪录片", "听播客", "阅读非专业书"
        )),
        PresetCategory("兴趣爱好", "🎨", "#A855F7", listOf(
            "画画", "弹琴", "摄影", "手工", "编程项目",
            "写博客", "剪视频", "下棋", "养植物"
        )),
        PresetCategory("医疗健康", "💊", "#F43F5E", listOf(
            "看病", "吃药", "体检", "心理咨询", "按摩/理疗"
        )),
        PresetCategory("无效时间", "😶", "#9CA3AF", listOf(
            "发呆", "无目的刷手机", "排队等待", "纠结犹豫", "闲逛", "无所事事"
        )),
        PresetCategory("电子设备", "💻", "#64748B", listOf(
            "修电脑", "整理文件", "系统维护", "装软件", "清理空间", "备份数据"
        ))
    )
}
