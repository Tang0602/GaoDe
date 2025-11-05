import subprocess
import json
import os
from datetime import datetime

def debug_file_creation():
    """调试文件创建过程"""
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
        
        device_file_path = 'files/1_message_history.json'
        history_records = [navigation_record]
        
        # 创建临时文件
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_navigation_debug.json')
        with open(temp_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        print(f"1. Created temp file: {temp_file}")
        
        # 检查临时文件内容
        with open(temp_file, 'r', encoding='utf-8') as f:
            content = f.read()
            print(f"2. Temp file content:\n{content}")
        
        # 推送文件到设备
        print("3. Pushing file to device...")
        push_result = subprocess.run(['adb', 'push', temp_file, '/sdcard/temp_navigation_debug.json'], 
                                   capture_output=True, text=True)
        
        print(f"4. Push result: {push_result.returncode}")
        if push_result.stdout:
            print(f"   Push stdout: {push_result.stdout}")
        if push_result.stderr:
            print(f"   Push stderr: {push_result.stderr}")
        
        if push_result.returncode == 0:
            # 检查文件是否在sdcard中
            check_result = subprocess.run(['adb', 'shell', 'ls', '-la', '/sdcard/temp_navigation_debug.json'], 
                                        capture_output=True, text=True)
            print(f"5. File exists in sdcard: {check_result.returncode == 0}")
            if check_result.stdout:
                print(f"   File info: {check_result.stdout}")
            
            # 将文件移动到应用内部存储
            print("6. Moving file to app storage...")
            move_result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 
                                        'cp', '/sdcard/temp_navigation_debug.json', device_file_path],
                                       capture_output=True, text=True)
            
            print(f"7. Move result: {move_result.returncode}")
            if move_result.stdout:
                print(f"   Move stdout: {move_result.stdout}")
            if move_result.stderr:
                print(f"   Move stderr: {move_result.stderr}")
            
            # 检查文件是否在应用目录中
            final_check = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 'ls', '-la', device_file_path],
                                       capture_output=True, text=True)
            print(f"8. File exists in app storage: {final_check.returncode == 0}")
            if final_check.stdout:
                print(f"   Final file info: {final_check.stdout}")
            if final_check.stderr:
                print(f"   Final check error: {final_check.stderr}")
            
            # 清理临时文件
            subprocess.run(['adb', 'shell', 'rm', '/sdcard/temp_navigation_debug.json'], capture_output=True)
            os.remove(temp_file)
            
            return move_result.returncode == 0
        else:
            print("Push failed, cannot continue")
            if os.path.exists(temp_file):
                os.remove(temp_file)
            return False
        
    except Exception as e:
        print(f"ERROR: {e}")
        return False

if __name__ == "__main__":
    debug_file_creation()