import subprocess
import json
import os
from datetime import datetime

def update_notification_history(action_type):
    """更新通知查看历史记录"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        notification_record = {
            "id": f"notification_{timestamp}",
            "action": action_type,
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "通知页面",
            "success": True
        }
        
        history_file = os.path.join(os.path.dirname(__file__), '8_通知历史.json')
        history_records = []
        
        if os.path.exists(history_file):
            try:
                with open(history_file, 'r', encoding='utf-8') as f:
                    history_records = json.load(f)
            except json.JSONDecodeError:
                history_records = []
        
        history_records.append(notification_record)
        
        with open(history_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        print(f"✅ 通知历史记录已更新: {action_type}")
        return True
        
    except Exception as e:
        print(f"更新通知历史失败: {e}")
        return False

def NotificationCheck():
    """检测是否成功查看我的退款取消"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True)
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            notification_indicators = [
                '退款', '取消', '申请退款', '订单取消', '退款状态',
                '处理中', '已退款', '退款金额', '取消原因'
            ]
            
            found_indicators = [indicator for indicator in notification_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"✓ 在UI中找到退款取消页面元素: {', '.join(found_indicators)}")
                update_notification_history("查看我的退款取消")
                return True
            else:
                print("✗ 未在UI中找到退款取消页面元素")
                return False
        else:
            print(f"UI检测失败: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"退款取消检测失败: {e}")
        return False

if __name__ == "__main__":
    print("开始检测：查看我的退款取消")
    result = NotificationCheck()
    print(f"检测结果: {'通过' if result else '失败'}")