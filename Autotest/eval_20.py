import subprocess
import json
import os
import base64
from datetime import datetime

def update_multi_route_history(route_action):
    """Update multi-point route planning history to device storage"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        route_record = {
            "id": f"multi_route_{timestamp}",
            "action": route_action,
            "routeType": "多地点复合路线",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "Multi-point Route Planning",
            "success": True
        }
        
        device_file_path = 'files/20_multi_route_history.json'
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
        
        history_records.append(route_record)
        
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_multi_route20.json')
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
                print(f"Multi-route history updated to device storage: {route_action}")
                return True
            else:
                print("File creation verification failed")
                return False
        else:
            print(f"Failed to create file: {create_result.stderr}")
            return False
        
    except Exception as e:
        print(f"Failed to update multi-route history: {e}")
        return False

def MultiPointRouteCheck():
    """Check if multi-point route planning is successful"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # Check for multi-point route planning UI elements
            route_indicators = [
                '多地点', '复合路线', '路线规划', '添加途经点',
                '多点导航', '途经', '规划路线', '多个地点'
            ]
            
            found_indicators = [indicator for indicator in route_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"SUCCESS: Found UI elements")
                if update_multi_route_history("Plan Multi-Point Composite Route"):
                    return True
                else:
                    print("X Multi-route history update failed")
                    return False
            else:
                print("X Multi-point route elements not found in UI")
                return False
        else:
            print(f"UI detection failed: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"Multi-point route detection failed: {e}")
        return False

if __name__ == "__main__":
    print("Starting detection: Plan Multi-Point Composite Route")
    result = MultiPointRouteCheck()
    print(f"Detection result: {'PASS' if result else 'FAIL'}")