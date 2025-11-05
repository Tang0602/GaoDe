import subprocess
import json
import os
from datetime import datetime

def update_huanghelou_route_history(action_type, destination):
    """更新去黄鹤楼路线规划历史记录"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        route_record = {
            "id": f"route_{timestamp}",
            "action": action_type,
            "destination": destination,
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "routeOptions": 4,
            "transportModes": ["公交", "打车+地铁", "轨道交通", "打车"],
            "success": True
        }
        
        history_file = os.path.join(os.path.dirname(__file__), '13_黄鹤楼路线历史.json')
        history_records = []
        
        if os.path.exists(history_file):
            try:
                with open(history_file, 'r', encoding='utf-8') as f:
                    history_records = json.load(f)
            except json.JSONDecodeError:
                history_records = []
        
        history_records.append(route_record)
        
        with open(history_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        print(f"✅ 黄鹤楼路线历史记录已更新: {action_type} - {destination}")
        return True
        
    except Exception as e:
        print(f"更新黄鹤楼路线历史失败: {e}")
        return False

def HuanghelouRouteCheck():
    """检测是否成功规划去黄鹤楼的路线"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            route_indicators = [
                '黄鹤楼', '路线规划', '打车', '公交', '地铁', '步行',
                '5号线', '11号线', '804路', '一口价', '换乘',
                '约', '分钟', '元起', '站'
            ]
            
            found_indicators = [indicator for indicator in route_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"✓ 在UI中找到黄鹤楼路线规划元素: {', '.join(found_indicators)}")
                update_huanghelou_route_history("规划去黄鹤楼的路线", "黄鹤楼")
                return True
            else:
                print("✗ 未在UI中找到黄鹤楼路线规划元素")
                return False
        else:
            print(f"UI检测失败: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"黄鹤楼路线规划检测失败: {e}")
        return False

if __name__ == "__main__":
    print("开始检测：规划去黄鹤楼的路线")
    result = HuanghelouRouteCheck()
    print(f"检测结果: {'通过' if result else '失败'}")