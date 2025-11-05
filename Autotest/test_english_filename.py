import subprocess
import json
import os
from datetime import datetime

def test_create_english_filename():
    """测试用英文文件名创建历史文件"""
    try:
        # 创建新的导航记录
        timestamp = int(datetime.now().timestamp() * 1000)
        navigation_record = {
            "id": f"nav_{timestamp}",
            "action": "点击消息页面",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "消息页面",
            "success": True
        }
        
        # 使用英文文件名
        device_file_path = 'files/1_message_history.json'
        history_records = [navigation_record]
        
        # 创建临时文件并推送到设备
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_message.json')
        with open(temp_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        print(f"Created temp file: {temp_file}")
        
        # 推送文件到设备
        push_result = subprocess.run(['adb', 'push', temp_file, '/sdcard/temp_message.json'], 
                                   capture_output=True, text=True)
        
        print(f"Push result: {push_result.returncode}")
        
        if push_result.returncode == 0:
            # 将文件移动到应用内部存储
            move_result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 
                                        'cp', '/sdcard/temp_message.json', device_file_path],
                                       capture_output=True, text=True)
            
            print(f"Move result: {move_result.returncode}")
            if move_result.stderr:
                print(f"Move stderr: {move_result.stderr}")
            
            # 清理临时文件
            subprocess.run(['adb', 'shell', 'rm', '/sdcard/temp_message.json'], capture_output=True)
            os.remove(temp_file)
            
            if move_result.returncode == 0:
                print("SUCCESS: English filename history file created!")
                return True
            else:
                print(f"FAILED: Could not move file to app storage")
                return False
        else:
            print(f"FAILED: Could not push file to device")
            if os.path.exists(temp_file):
                os.remove(temp_file)
            return False
        
    except Exception as e:
        print(f"ERROR: {e}")
        return False

if __name__ == "__main__":
    test_create_english_filename()