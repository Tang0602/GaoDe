import subprocess
import json
import os
import base64
from datetime import datetime

def update_restaurant_filter_history(filter_action):
    """Update restaurant filtering history to device storage"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        filter_record = {
            "id": f"restaurant_{timestamp}",
            "action": filter_action,
            "filterType": "综合筛选最优餐厅",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "Restaurant Filter",
            "success": True
        }
        
        device_file_path = 'files/22_restaurant_filter_history.json'
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
        
        history_records.append(filter_record)
        
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_restaurant22.json')
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
                print(f"Restaurant filter history updated to device storage: {filter_action}")
                return True
            else:
                print("File creation verification failed")
                return False
        else:
            print(f"Failed to create file: {create_result.stderr}")
            return False
        
    except Exception as e:
        print(f"Failed to update restaurant filter history: {e}")
        return False

def RestaurantFilterCheck():
    """Check if comprehensive restaurant filtering is successful"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # Check for comprehensive restaurant filtering and selection
            has_filter_interface = any(filter_elem in ui_dump for filter_elem in ['筛选', '过滤', '排序', '条件'])
            has_restaurant_info = any(rest in ui_dump for rest in ['餐厅', '美食', '饭店', '菜系'])
            has_multiple_criteria = sum(1 for criteria in ['评分', '价格', '距离', '人气', '口味'] if criteria in ui_dump) >= 2
            has_selection_made = any(select in ui_dump for select in ['选择', '确定', '预订', '已选择', '查看详情'])
            
            if has_filter_interface and has_restaurant_info and has_multiple_criteria and has_selection_made:
                print(f"SUCCESS: Found UI elements")
                if update_restaurant_filter_history("Comprehensive Filter for Best Restaurant"):
                    return True
                else:
                    print("X Restaurant filter history update failed")
                    return False
            else:
                if not has_filter_interface:
                    print("X Filter interface not found")
                elif not has_restaurant_info:
                    print("X Restaurant information not found")
                elif not has_multiple_criteria:
                    print("X Multiple filter criteria not found")
                else:
                    print("X Restaurant selection not made")
                return False
        else:
            print(f"UI detection failed: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"Restaurant filter detection failed: {e}")
        return False

if __name__ == "__main__":
    print("Starting detection: Comprehensive Filter for Best Restaurant")
    result = RestaurantFilterCheck()
    print(f"Detection result: {'PASS' if result else 'FAIL'}")