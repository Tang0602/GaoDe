import subprocess
import json
import os
from datetime import datetime

def update_home_navigation_history(navigation_action):
    """更新返回首页历史记录"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        home_record = {
            "id": f"home_{timestamp}",
            "action": navigation_action,
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "首页",
            "success": True
        }
        
        history_file = os.path.join(os.path.dirname(__file__), '4_首页历史.json')
        history_records = []
        
        if os.path.exists(history_file):
            try:
                with open(history_file, 'r', encoding='utf-8') as f:
                    history_records = json.load(f)
            except json.JSONDecodeError:
                history_records = []
        
        history_records.append(home_record)
        
        with open(history_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        print(f"✅ 首页导航历史记录已更新: {navigation_action}")
        return True
        
    except Exception as e:
        print(f"更新首页导航历史失败: {e}")
        return False

def HomePageNavigationCheck():
    """检测是否成功返回首页"""
    try:
        # 检查UI中是否存在首页特有元素
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True)
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            # 检查首页特有的UI元素
            home_indicators = [
                '武汉站', '高德地图', '公交', '地铁', '骑行', 
                '打车', '步行', '订酒店', '回家', '去单位'
            ]
            
            found_indicators = [indicator for indicator in home_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"✓ 在UI中找到首页元素: {', '.join(found_indicators)}")
                update_home_navigation_history("返回首页")
                return True
            else:
                print("✗ 未在UI中找到首页元素")
                return False
        else:
            print(f"UI检测失败: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"首页导航检测失败: {e}")
        return False

if __name__ == "__main__":
    print("开始检测：返回首页")
    result = HomePageNavigationCheck()
    print(f"检测结果: {'通过' if result else '失败'}")