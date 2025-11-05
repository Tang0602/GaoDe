import subprocess
import json
import os
import base64
from datetime import datetime

def update_favorite_history(action_type):
    """Update favorite restaurant history to device storage"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        favorite_record = {
            "id": f"favorite_{timestamp}",
            "action": action_type,
            "restaurantName": "老乡鸡（路啰路店）",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "category": "中式快餐",
            "location": "路啰路店",
            "page": "Favorite Restaurant",
            "success": True
        }
        
        device_file_path = 'files/11_favorite_restaurant_history.json'
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
        
        history_records.append(favorite_record)
        
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_favorite11.json')
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
                print(f"Favorite restaurant history updated to device storage: {action_type}")
                return True
            else:
                print("File creation verification failed")
                return False
        else:
            print(f"Failed to create file: {create_result.stderr}")
            return False
        
    except Exception as e:
        print(f"Failed to update favorite restaurant history: {e}")
        return False

def FavoriteRestaurantCheck():
    """Check if favorite restaurant action is successful"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # Check for favorite restaurant elements
            favorite_indicators = [
                '老乡鸡', '路啰路店', '收藏', '已收藏',
                '中式快餐', '餐厅', '收藏成功', '添加收藏'
            ]
            
            found_indicators = [indicator for indicator in favorite_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"SUCCESS: Found UI elements")
                if update_favorite_history("Favorite Laoxiangji Restaurant"):
                    return True
                else:
                    print("X Favorite restaurant history update failed")
                    return False
            else:
                print("X Favorite restaurant elements not found in UI")
                return False
        else:
            print(f"UI detection failed: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"Favorite restaurant detection failed: {e}")
        return False

if __name__ == "__main__":
    print("Starting detection: Favorite Laoxiangji Restaurant")
    result = FavoriteRestaurantCheck()
    print(f"Detection result: {'PASS' if result else 'FAIL'}")