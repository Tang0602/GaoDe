import subprocess
import json
import os
from datetime import datetime

def update_parent_chat_history(action_type):
    """更新与爸妈聊天界面历史记录"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        chat_record = {
            "id": f"chat_{timestamp}",
            "action": action_type,
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "家人聊天界面",
            "success": True
        }
        
        history_file = os.path.join(os.path.dirname(__file__), '7_家人聊天历史.json')
        history_records = []
        
        if os.path.exists(history_file):
            try:
                with open(history_file, 'r', encoding='utf-8') as f:
                    history_records = json.load(f)
            except json.JSONDecodeError:
                history_records = []
        
        history_records.append(chat_record)
        
        with open(history_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        print(f"✅ 家人聊天历史记录已更新: {action_type}")
        return True
        
    except Exception as e:
        print(f"更新家人聊天历史失败: {e}")
        return False

def ParentChatPageCheck():
    """检测是否成功查看与爸妈的聊天界面"""
    try:
        # 检查UI中是否存在家人聊天页面元素
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True)
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            # 检查家人聊天页面特有的UI元素
            chat_indicators = [
                '爸爸', '妈妈', '路上注意安全', '今天吃什么',
                '华中科技大学', '南湖花溪公园', '位置分享',
                '输入消息', '发送'
            ]
            
            found_indicators = [indicator for indicator in chat_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"✓ 在UI中找到家人聊天界面元素: {', '.join(found_indicators)}")
                update_parent_chat_history("查看与爸妈的聊天界面")
                return True
            else:
                print("✗ 未在UI中找到家人聊天界面元素")
                return False
        else:
            print(f"UI检测失败: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"家人聊天界面检测失败: {e}")
        return False

if __name__ == "__main__":
    print("开始检测：查看与爸妈的聊天界面")
    result = ParentChatPageCheck()
    print(f"检测结果: {'通过' if result else '失败'}")