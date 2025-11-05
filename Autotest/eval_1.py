import subprocess
import json
import os
import base64
from datetime import datetime

def update_navigation_history(navigation_action):
    """更新导航历史记录到设备内部存储"""
    try:
        # 创建新的导航记录
        timestamp = int(datetime.now().timestamp() * 1000)
        navigation_record = {
            "id": f"nav_{timestamp}",
            "action": navigation_action,
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "消息页面",
            "success": True
        }
        
        # 从设备读取现有历史记录（使用英文文件名）
        device_file_path = 'files/1_message_history.json'
        history_records = []
        
        try:
            # 尝试从设备获取现有历史记录
            result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 'cat', device_file_path],
                                   stdout=subprocess.PIPE, stderr=subprocess.PIPE, encoding='utf-8', errors='ignore')
            if result.returncode == 0 and result.stdout and result.stdout.strip():
                try:
                    history_records = json.loads(result.stdout)
                except json.JSONDecodeError:
                    history_records = []
        except Exception:
            history_records = []
        
        # 添加新记录到历史
        history_records.append(navigation_record)
        
        # 创建临时文件并推送到设备
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_navigation.json')
        with open(temp_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        # 读取临时文件内容并进行base64编码
        with open(temp_file, 'r', encoding='utf-8') as f:
            json_content = f.read()
        
        # 使用base64编码来避免字符编码问题
        json_bytes = json_content.encode('utf-8')
        json_b64 = base64.b64encode(json_bytes).decode('ascii')
        
        # 使用base64解码创建文件
        create_result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 
                                      'sh', '-c', f'echo "{json_b64}" | base64 -d > {device_file_path}'],
                                     capture_output=True, text=True)
        
        # 清理临时文件
        os.remove(temp_file)
        
        if create_result.returncode == 0:
            # 验证文件是否真的创建了
            verify_result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 
                                          'test', '-f', device_file_path],
                                         capture_output=True, text=True)
            
            if verify_result.returncode == 0:
                print(f"Navigation history updated to device storage: {navigation_action}")
                return True
            else:
                print("File creation verification failed")
                return False
        else:
            print(f"Failed to create file: {create_result.stderr}")
            return False
        
    except Exception as e:
        print(f"Failed to update navigation history: {e}")
        return False

def MessagePageNavigationCheck():
    """检测是否成功点击进入消息页面"""
    try:
        # 使用ADB检查当前页面状态
        result = subprocess.run(['adb', 'shell', 'dumpsys', 'activity', 'activities'], 
                              capture_output=True, text=True)
        
        if result.returncode == 0:
            output = result.stdout
            
            # 检查是否在消息页面相关的Activity或Fragment
            if any(keyword in output for keyword in ['MessageScreen', 'MessagePage', 'message']):
                print("SUCCESS: Successfully entered message page")
                # 更新导航历史
                if update_navigation_history("点击消息页面"):
                    return True
                else:
                    print("X Navigation history update failed")
                    return False
            else:
                print("X Message page not found")
                return False
        else:
            print(f"ADB command failed: {result.stderr}")
            # 备用检测方法：检查UI元素
            return check_message_ui_elements()
            
    except Exception as e:
        print(f"Message page navigation detection failed: {e}")
        return check_message_ui_elements()

def check_message_ui_elements():
    """备用方法：检查消息页面的UI元素"""
    try:
        # 获取当前UI层次结构
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # 检查消息页面特有的UI元素
            message_indicators = [
                '地点评论', '通知权限提醒', '订单提醒', '打车会话',
                '公共交通动态', '天天领福利', '高德运动', '高德快报'
            ]
            
            found_indicators = [indicator for indicator in message_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"SUCCESS: Found message page elements in UI: {', '.join(found_indicators)}")
                if update_navigation_history("点击消息页面"):
                    return True
                else:
                    print("X Navigation history update failed")
                    return False
            else:
                print("X Message page elements not found in UI")
                return False
        else:
            print(f"UI detection failed: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"UI element detection failed: {e}")
        return False

if __name__ == "__main__":
    print("Starting detection: Click message page")
    result = MessagePageNavigationCheck()
    print(f"Detection result: {'PASS' if result else 'FAIL'}")