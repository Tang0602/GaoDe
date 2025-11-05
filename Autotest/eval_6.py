import subprocess
import json
import os
from datetime import datetime

def update_profile_history(action_type):
    """更新个人中心访问历史记录"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        profile_record = {
            "id": f"profile_{timestamp}",
            "action": action_type,
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "个人中心",
            "success": True
        }
        
        history_file = os.path.join(os.path.dirname(__file__), '6_个人中心历史.json')
        history_records = []
        
        if os.path.exists(history_file):
            try:
                with open(history_file, 'r', encoding='utf-8') as f:
                    history_records = json.load(f)
            except json.JSONDecodeError:
                history_records = []
        
        history_records.append(profile_record)
        
        with open(history_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        print(f"✅ 个人中心历史记录已更新: {action_type}")
        return True
        
    except Exception as e:
        print(f"更新个人中心历史失败: {e}")
        return False

def ProfilePageCheck():
    """检测是否成功查看个人中心"""
    try:
        # 检查UI中是否存在个人中心页面元素
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True)
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            # 检查个人中心页面特有的UI元素
            profile_indicators = [
                '语音包', '车标', '皮肤', '全部订单', '退款', '取消',
                '待评价', '收藏夹', '语音包', '添加爱车', '点这里'
            ]
            
            found_indicators = [indicator for indicator in profile_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"✓ 在UI中找到个人中心元素: {', '.join(found_indicators)}")
                update_profile_history("查看个人中心")
                return True
            else:
                print("✗ 未在UI中找到个人中心元素")
                return False
        else:
            print(f"UI检测失败: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"个人中心检测失败: {e}")
        return False

if __name__ == "__main__":
    print("开始检测：查看个人中心")
    result = ProfilePageCheck()
    print(f"检测结果: {'通过' if result else '失败'}")