package com.example.GaoDe.ui.home

import android.content.Context
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.BusRouteResult
import com.amap.api.services.route.DriveRouteResult
import com.amap.api.services.route.RideRouteResult
import com.amap.api.services.route.RouteSearch
import com.amap.api.services.route.WalkRouteResult
import com.example.GaoDe.model.RouteOption
import com.example.GaoDe.model.RouteSegment
import kotlinx.coroutines.*

class PlanRoutePresenter(
    private val view: PlanRouteContract.View,
    private val context: Context? = null
) : PlanRouteContract.Presenter, RouteSearch.OnRouteSearchListener {

    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var routeSearch: RouteSearch? = null

    override fun start() {
        // 初始化高德路线规划搜索
        if (context != null) {
            try {
                routeSearch = RouteSearch(context)
                routeSearch?.setRouteSearchListener(this)
            } catch (e: Exception) {
                view.showError("路线搜索初始化失败: ${e.message}")
            }
        }
    }

    override fun stop() {
        presenterScope.cancel()
        routeSearch = null
    }

    override fun searchBusRoute(startLat: Double, startLon: Double, endLat: Double, endLon: Double) {
        view.showLoading()

        presenterScope.launch {
            try {
                // 构造起点和终点
                val startPoint = LatLonPoint(startLat, startLon)
                val endPoint = LatLonPoint(endLat, endLon)

                // 创建路线查询对象
                val fromAndTo = RouteSearch.FromAndTo(startPoint, endPoint)

                // 创建公交路线查询参数
                // 第三个参数是查询模式：0-最快捷, 1-最经济, 2-最少换乘, 3-最少步行, 4-最舒适, 5-不乘地铁
                val busQuery = RouteSearch.BusRouteQuery(
                    fromAndTo,
                    RouteSearch.BusDefault,  // 使用默认模式（最快捷）
                    "武汉",  // 城市名称
                    0  // 第几页，从0开始
                )

                // 发起异步查询
                withContext(Dispatchers.IO) {
                    routeSearch?.calculateBusRouteAsyn(busQuery)
                }
            } catch (e: Exception) {
                view.hideLoading()
                view.showError("路线查询失败: ${e.message}")
            }
        }
    }

    // 高德公交路线搜索结果回调
    override fun onBusRouteSearched(result: BusRouteResult?, errorCode: Int) {
        presenterScope.launch {
            if (errorCode == 1000 && result != null) {
                try {
                    // 解析高德SDK返回的公交路线数据
                    val routeOptions = parseBusRouteResult(result)

                    view.hideLoading()
                    if (routeOptions.isNotEmpty()) {
                        view.showRouteOptions(routeOptions)
                    } else {
                        view.showError("未找到合适的公交路线")
                    }
                } catch (e: Exception) {
                    view.hideLoading()
                    view.showError("路线数据解析失败: ${e.message}")
                }
            } else {
                view.hideLoading()
                val errorMsg = when (errorCode) {
                    1001 -> "查询参数错误"
                    1002 -> "网络连接失败"
                    1003 -> "协议解析错误"
                    1800 -> "起点或终点无效"
                    1801 -> "路线规划失败，未找到可用路线"
                    1802 -> "路线规划失败，参数错误"
                    1803 -> "路线规划失败，起终点距离过近"
                    else -> "路线查询失败，错误码: $errorCode"
                }
                view.showError(errorMsg)
            }
        }
    }

    // 解析公交路线结果，将高德SDK数据转换为应用的RouteOption格式
    private fun parseBusRouteResult(result: BusRouteResult): List<RouteOption> {
        val routeOptions = mutableListOf<RouteOption>()

        // 获取所有公交路线方案
        val busPaths = result.paths ?: return routeOptions

        // 遍历每一条路线方案
        busPaths.forEachIndexed { index, busPath ->
            try {
                // 计算总时长（秒转分钟）
                val durationMinutes = (busPath.duration / 60).toInt()
                val durationText = if (durationMinutes >= 60) {
                    val hours = durationMinutes / 60
                    val minutes = durationMinutes % 60
                    "${hours}小时${minutes}分钟"
                } else {
                    "${durationMinutes}分钟"
                }

                // 计算总距离（米转公里）
                val distanceKm = String.format("%.1f", busPath.distance / 1000.0)
                val distanceText = "${distanceKm}公里"

                // 计算费用
                val cost = String.format("%.0f", busPath.cost)
                val priceText = "${cost}元"

                // 解析路线段
                val segments = parseRouteSegments(busPath)

                // 生成路线详情描述
                val details = generateRouteDetails(busPath, segments)

                // 生成标签
                val tags = generateRouteTags(busPath, index)

                // 创建RouteOption对象
                val routeOption = RouteOption(
                    id = "route_${index + 1}",
                    transportationType = "公交",
                    duration = durationText,
                    distance = distanceText,
                    price = priceText,
                    mainRoute = segments,
                    details = details,
                    realTimeInfo = null,  // 暂不支持实时信息
                    tags = tags,
                    isRecommended = index == 0  // 第一条路线推荐
                )

                routeOptions.add(routeOption)
            } catch (e: Exception) {
                // 解析单条路线失败，跳过继续解析下一条
            }
        }

        return routeOptions
    }

    // 解析路线段：将BusStep转换为RouteSegment
    private fun parseRouteSegments(busPath: com.amap.api.services.route.BusPath): List<RouteSegment> {
        val segments = mutableListOf<RouteSegment>()

        val steps = busPath.steps ?: return segments

        steps.forEach { busStep ->
            try {
                when {
                    // 步行段
                    busStep.walk != null && busStep.walk.steps.isNotEmpty() -> {
                        val walkDistance = (busStep.walk.distance / 1000.0)
                        val walkTime = (busStep.walk.duration / 60).toInt()

                        if (walkDistance >= 0.01 || walkTime >= 1) {  // 忽略太短的步行
                            val walkText = if (walkDistance >= 1.0) {
                                "${String.format("%.1f", walkDistance)}公里"
                            } else {
                                "${(busStep.walk.distance).toInt()}米"
                            }

                            segments.add(
                                RouteSegment(
                                    type = "步行",
                                    description = "$walkText",
                                    icon = "walk",
                                    backgroundColor = null
                                )
                            )
                        }
                    }

                    // 公交段
                    busStep.busLines != null && busStep.busLines.isNotEmpty() -> {
                        val busLine = busStep.busLines[0]
                        val lineName = busLine.busLineName ?: "公交"
                        val passStationNum = busLine.passStationNum

                        segments.add(
                            RouteSegment(
                                type = "公交",
                                description = lineName,
                                icon = "bus",
                                backgroundColor = "绿色"
                            )
                        )
                    }

                    // 地铁段
                    busStep.railway != null -> {
                        val railway = busStep.railway
                        val lineName = railway.name ?: "地铁"

                        segments.add(
                            RouteSegment(
                                type = "地铁",
                                description = lineName,
                                icon = "subway",
                                backgroundColor = "蓝色"
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // 解析单个步骤失败，跳过
            }
        }

        return segments
    }

    // 生成路线详情描述
    private fun generateRouteDetails(busPath: com.amap.api.services.route.BusPath, segments: List<RouteSegment>): String {
        val details = StringBuilder()

        val steps = busPath.steps ?: return ""

        // 统计换乘次数
        var busTransferCount = 0
        var lastWasBusOrRailway = false

        steps.forEach { busStep ->
            if ((busStep.busLines != null && busStep.busLines.isNotEmpty()) || busStep.railway != null) {
                if (lastWasBusOrRailway) {
                    busTransferCount++
                }
                lastWasBusOrRailway = true
            } else {
                lastWasBusOrRailway = false
            }
        }

        // 获取第一个乘车站点信息
        val firstBusStep = steps.firstOrNull { step ->
            step.busLines != null && step.busLines.isNotEmpty()
        }

        if (firstBusStep != null && firstBusStep.busLines.isNotEmpty()) {
            val busLine = firstBusStep.busLines[0]
            // 使用departureBusStation获取起点站信息
            val firstStation = busLine.departureBusStation?.busStationName
            if (firstStation != null) {
                details.append("$firstStation 上车")
            }
        }

        // 添加换乘信息
        if (busTransferCount > 0) {
            if (details.isNotEmpty()) details.append(" · ")
            details.append("换乘${busTransferCount}次")
        }

        // 添加总费用
        val cost = String.format("%.0f", busPath.cost)
        if (details.isNotEmpty()) details.append(" · ")
        details.append("${cost}元")

        return details.toString()
    }

    // 生成路线标签
    private fun generateRouteTags(busPath: com.amap.api.services.route.BusPath, index: Int): List<String> {
        val tags = mutableListOf<String>()

        val steps = busPath.steps ?: return tags

        // 统计换乘次数
        var transferCount = 0
        var lastWasBusOrRailway = false

        steps.forEach { busStep ->
            if ((busStep.busLines != null && busStep.busLines.isNotEmpty()) || busStep.railway != null) {
                if (lastWasBusOrRailway) {
                    transferCount++
                }
                lastWasBusOrRailway = true
            } else {
                lastWasBusOrRailway = false
            }
        }

        // 添加换乘标签
        if (transferCount == 0) {
            tags.add("直达")
        } else if (transferCount <= 1) {
            tags.add("换乘${transferCount}次")
        }

        // 第一条路线推荐
        if (index == 0) {
            tags.add("推荐")
        }

        // 判断是否有地铁
        val hasRailway = steps.any { it.railway != null }
        if (hasRailway) {
            tags.add("地铁直达")
        }

        return tags
    }

    // 以下回调暂不实现
    override fun onDriveRouteSearched(result: DriveRouteResult?, errorCode: Int) {}
    override fun onWalkRouteSearched(result: WalkRouteResult?, errorCode: Int) {}
    override fun onRideRouteSearched(result: RideRouteResult?, errorCode: Int) {}
}
