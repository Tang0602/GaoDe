import subprocess
import json
import os
import base64
from datetime import datetime

def update_review_analysis_history(analysis_action):
    """Update review analysis history to device storage"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        analysis_record = {
            "id": f"review_{timestamp}",
            "action": analysis_action,
            "analysisType": "评论分析选择最佳景点",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "Review Analysis",
            "success": True
        }
        
        device_file_path = 'files/21_review_analysis_history.json'
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
        
        history_records.append(analysis_record)
        
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_review21.json')
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
                print(f"Review analysis history updated to device storage: {analysis_action}")
                return True
            else:
                print("File creation verification failed")
                return False
        else:
            print(f"Failed to create file: {create_result.stderr}")
            return False
        
    except Exception as e:
        print(f"Failed to update review analysis history: {e}")
        return False

def ReviewAnalysisCheck():
    """Check if review analysis for best attraction selection is successful"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # Check for review analysis and attraction selection
            has_reviews_interface = any(review in ui_dump for review in ['评论', '用户评价', '点评', '评分'])
            has_attraction_info = any(attr in ui_dump for attr in ['景点', '景区', '旅游', '门票'])
            has_selection_made = any(select in ui_dump for select in ['选择', '确定', '预订', '购买门票', '已选择'])
            
            if has_reviews_interface and has_attraction_info and has_selection_made:
                print(f"SUCCESS: Found UI elements")
                if update_review_analysis_history("Analyze Reviews and Select Best Attraction"):
                    return True
                else:
                    print("X Review analysis history update failed")
                    return False
            else:
                if not has_reviews_interface:
                    print("X Reviews interface not found")
                elif not has_attraction_info:
                    print("X Attraction information not found")
                else:
                    print("X Selection not made")
                return False
        else:
            print(f"UI detection failed: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"Review analysis detection failed: {e}")
        return False

if __name__ == "__main__":
    print("Starting detection: Analyze Reviews and Select Best Attraction")
    result = ReviewAnalysisCheck()
    print(f"Detection result: {'PASS' if result else 'FAIL'}")