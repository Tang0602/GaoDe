import subprocess
import json
import os
import base64
from datetime import datetime

def update_ride_history(ride_action):
    """Update ride and message history to device storage"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        ride_record = {
            "id": f"ride_{timestamp}",
            "action": ride_action,
            "destination": "武汉海昌极地公园",
            "messageToDriver": "我到了",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "Ride",
            "success": True
        }
        
        device_file_path = 'files/18_ride_history.json'
        history_records = []
        
        try:
            result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 'cat', device_file_path],
                                   stdout=subprocess.PIPE, stderr=subprocess.PIPE, encoding='utf-8', errors='ignore')
            if result.returncode == 0 and result.stdout and result.stdout.strip():
                try:
                    history_records = json.loads(result.stdout)
                except json.JSONDecodeError:
                    history_records = []
        except Exception:
            history_records = []
        
        history_records.append(ride_record)
        
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_ride18.json')
        with open(temp_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        with open(temp_file, 'r', encoding='utf-8') as f:
            json_content = f.read()
        
        json_bytes = json_content.encode('utf-8')
        json_b64 = base64.b64encode(json_bytes).decode('ascii')
        
        create_result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 
                                      'sh', '-c', f'echo "{json_b64}" | base64 -d > {device_file_path}'],
                                     capture_output=True, text=True)
        
        os.remove(temp_file)
        
        if create_result.returncode == 0:
            verify_result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 
                                          'test', '-f', device_file_path],
                                         capture_output=True, text=True)
            
            if verify_result.returncode == 0:
                print(f"Ride history updated to device storage: {ride_action}")
                return True
            else:
                print("File creation verification failed")
                return False
        else:
            print(f"Failed to create file: {create_result.stderr}")
            return False
        
    except Exception as e:
        print(f"Failed to update ride history: {e}")
        return False

def RideToHaichangCheck():
    """Check if ride to Wuhan Haichang Polar Park and message to driver is successful"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # Check if we're in chat interface with driver after completing ride
            in_driver_chat = '田师傅' in ui_dump and any(chat_elem in ui_dump for chat_elem in ['聊天', '发送', '消息'])
            has_arrival_message = '我到了' in ui_dump
            
            # Must be in chat with specific driver and have sent arrival message
            if in_driver_chat and has_arrival_message:
                print(f"SUCCESS: Found UI elements")
                if update_ride_history("Ride to Haichang Polar Park and Message Driver"):
                    return True
                else:
                    print("X Ride history update failed")
                    return False
            else:
                if not in_driver_chat:
                    print("X Not in chat interface with driver Tian Shifu")
                else:
                    print("X Arrival message not found")
                return False
        else:
            print(f"UI detection failed: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"Ride detection failed: {e}")
        return False

if __name__ == "__main__":
    print("Starting detection: Ride to Haichang Polar Park and Message Driver")
    result = RideToHaichangCheck()
    print(f"Detection result: {'PASS' if result else 'FAIL'}")