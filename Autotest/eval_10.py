import subprocess
import json
import os
from datetime import datetime

def update_home_navigation_history_10(action_type):
    """更新回家导航历史记录"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        home_nav_record = {
            "id": f"home_nav_{timestamp}",
            "action": action_type,
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "destination": "家",
            "success": True
        }
        
        history_file = os.path.join(os.path.dirname(__file__), '10_回家历史.json')
        history_records = []
        
        if os.path.exists(history_file):
            try:
                with open(history_file, 'r', encoding='utf-8') as f:
                    history_records = json.load(f)
            except json.JSONDecodeError:
                history_records = []
        
        history_records.append(home_nav_record)
        
        with open(history_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        print(f"✅ 回家导航历史记录已更新: {action_type}")
        return True
        
    except Exception as e:
        print(f"更新回家导航历史失败: {e}")
        return False

def HomeNavigationCheck():
    """检测是否成功触发回家导航"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True)
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            home_nav_indicators = [
                '导航成功', '回家', '路线规划', '开始导航',
                '到达目的地', '导航中', '家'
            ]
            
            found_indicators = [indicator for indicator in home_nav_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"✓ 在UI中找到回家导航元素: {', '.join(found_indicators)}")
                update_home_navigation_history_10("回家")
                return True
            else:
                print("✗ 未在UI中找到回家导航元素")
                return False
        else:
            print(f"UI检测失败: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"回家导航检测失败: {e}")
        return False

if __name__ == "__main__":
    print("开始检测：回家")
    result = HomeNavigationCheck()
    print(f"检测结果: {'通过' if result else '失败'}")