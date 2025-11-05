#!/usr/bin/env python3
"""
Script to generate fixed versions of all eval files with:
1. Formatted JSON output 
2. English error messages
3. Base64 encoding for device file transfer
"""

import os

# Base template for device file operations
device_file_template = '''
        device_file_path = 'files/{file_num}_{file_name}_history.json'
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
        
        history_records.append({record_name})
        
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_{temp_name}.json')
        with open(temp_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        with open(temp_file, 'r', encoding='utf-8') as f:
            json_content = f.read()
        
        json_bytes = json_content.encode('utf-8')
        json_b64 = base64.b64encode(json_bytes).decode('ascii')
        
        create_result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 
                                      'sh', '-c', f'echo "{{json_b64}}" | base64 -d > {{device_file_path}}'],
                                     capture_output=True, text=True)
        
        os.remove(temp_file)
        
        if create_result.returncode == 0:
            verify_result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 
                                          'test', '-f', device_file_path],
                                         capture_output=True, text=True)
            
            if verify_result.returncode == 0:
                print(f"{success_message}: {{action_param}}")
                return True
            else:
                print("File creation verification failed")
                return False
        else:
            print(f"Failed to create file: {{create_result.stderr}}")
            return False
'''

# Configuration for each eval file
eval_configs = [
    {
        'num': 5,
        'name': 'order_list',
        'function_name': 'update_order_history',
        'check_function': 'OrderListCheck', 
        'record_variable': 'order_record',
        'action_param': 'action_type',
        'success_message': 'Order list history updated to device storage',
        'detection_message': 'View My Order List',
        'ui_indicators': ['My Orders', 'All', 'Completed', 'In Progress', 'Cancelled', 'Taxi', 'Hotel', 'Refuel', 'Driver', 'Tickets']
    },
    {
        'num': 6,
        'name': 'profile',
        'function_name': 'update_profile_history',
        'check_function': 'ProfilePageCheck',
        'record_variable': 'profile_record', 
        'action_param': 'action_type',
        'success_message': 'Profile history updated to device storage',
        'detection_message': 'View Personal Center',
        'ui_indicators': ['Voice Pack', 'Car Icon', 'Theme', 'All Orders', 'Refund', 'Cancel', 'Review', 'Favorites', 'Add Car']
    },
    {
        'num': 7,
        'name': 'parent_chat',
        'function_name': 'update_parent_chat_history',
        'check_function': 'ParentChatPageCheck',
        'record_variable': 'chat_record',
        'action_param': 'action_type', 
        'success_message': 'Parent chat history updated to device storage',
        'detection_message': 'View Parent Chat Interface',
        'ui_indicators': ['Dad', 'Mom', 'Safety', 'Eat', 'University', 'Park', 'Location Share', 'Send Message']
    },
    {
        'num': 8,
        'name': 'refund_cancel',
        'function_name': 'update_refund_history',
        'check_function': 'RefundCancelCheck',
        'record_variable': 'refund_record',
        'action_param': 'action_type',
        'success_message': 'Refund cancel history updated to device storage', 
        'detection_message': 'View My Refund Cancel',
        'ui_indicators': ['Refund', 'Cancel', 'Apply Refund', 'Order Cancel', 'Refund Status', 'Processing', 'Refunded']
    },
    {
        'num': 9,
        'name': 'hotel_search', 
        'function_name': 'update_hotel_search_history',
        'check_function': 'HotelSearchCheck',
        'record_variable': 'search_record',
        'action_param': 'search_query',
        'success_message': 'Hotel search history updated to device storage',
        'detection_message': 'View Hotel List',
        'ui_indicators': ['Hanting Hotel', 'Home Inn', 'Hyatt', 'Economy', 'Business', 'Luxury', 'Book', 'Price', 'WiFi', '24 Hours']
    },
    {
        'num': 10,
        'name': 'home_navigation',
        'function_name': 'update_home_nav_history', 
        'check_function': 'HomeNavigationCheck',
        'record_variable': 'nav_record',
        'action_param': 'action_type',
        'success_message': 'Home navigation history updated to device storage',
        'detection_message': 'Go Home',
        'ui_indicators': ['Navigation Success', 'Home', 'Route Planning', 'Start Navigation', 'Destination', 'Navigating']
    }
]

# Generate fixed files
for config in eval_configs:
    file_content = f'''import subprocess
import json
import os
import base64
from datetime import datetime

def {config['function_name']}({config['action_param']}):
    """Update {config['name']} history to device storage"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        {config['record_variable']} = {{
            "id": f"{{config['name'][:3]}}_{timestamp}",
            "action": {config['action_param']},
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "{config['name'].replace('_', ' ').title()} Page",
            "success": True
        }}
        {device_file_template.format(
            file_num=config['num'],
            file_name=config['name'],
            record_name=config['record_variable'],
            temp_name=config['name'],
            success_message=config['success_message']
        )}
        
    except Exception as e:
        print(f"Failed to update {config['name']} history: {{e}}")
        return False

def {config['check_function']}():
    """Check if {config['detection_message'].lower()} was successful"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True)
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            {config['name']}_indicators = {config['ui_indicators']}
            
            found_indicators = [indicator for indicator in {config['name']}_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"SUCCESS: Found {config['name']} page elements in UI: {{', '.join(found_indicators)}}")
                if {config['function_name']}("{config['detection_message']}"):
                    return True
                else:
                    print("X {config['name'].title()} history update failed")
                    return False
            else:
                print("X {config['name'].title()} page elements not found in UI")
                return False
        else:
            print(f"UI detection failed: {{result.stderr}}")
            return False
            
    except Exception as e:
        print(f"{config['name'].title()} page detection failed: {{e}}")
        return False

if __name__ == "__main__":
    print("Starting detection: {config['detection_message']}")
    result = {config['check_function']}()
    print(f"Detection result: {{'PASS' if result else 'FAIL'}}")
'''
    
    # Write to file
    with open(f'eval_{config["num"]}.py', 'w', encoding='utf-8') as f:
        f.write(file_content)
    
    print(f"Generated eval_{config['num']}.py")

print("All fixed files generated successfully!")