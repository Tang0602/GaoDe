import subprocess
import json
import os
from datetime import datetime

def format_and_update_shared_messages(user_messages):
    """格式化消息并更新设备上的shared_messages.json文件"""
    try:
        # 处理和格式化用户消息数据
        formatted_messages = []
        for msg in user_messages:
            timestamp = datetime.fromtimestamp(msg['timestamp'] / 1000)
            
            # 如果消息已经是格式化的，保持原样；否则进行格式化
            if 'formattedTime' in msg:
                formatted_messages.append(msg)
            else:
                formatted_msg = {
                    "id": msg['id'],
                    "contactId": msg['contactId'],
                    "contactName": msg['contactName'],
                    "message": msg['message'],
                    "timestamp": msg['timestamp'],
                    "formattedTime": timestamp.strftime("%Y-%m-%d %H:%M:%S"),
                    "date": timestamp.strftime("%Y-%m-%d"),
                    "time": timestamp.strftime("%H:%M:%S")
                }
                formatted_messages.append(formatted_msg)
        
        # 按时间排序（最新的在前）
        formatted_messages.sort(key=lambda x: x['timestamp'], reverse=True)
        
        # 将格式化数据保存到临时文件
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_formatted.json')
        with open(temp_file, 'w', encoding='utf-8') as f:
            json.dump(formatted_messages, f, ensure_ascii=False, indent=2)
        
        # 使用ADB推送文件到设备
        push_result = subprocess.run(['adb', 'push', temp_file, '/sdcard/temp_messages.json'], 
                                   capture_output=True, text=True)
        
        if push_result.returncode == 0:
            # 将文件移动到应用目录
            move_result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 
                                        'cp', '/sdcard/temp_messages.json', 'files/shared_messages.json'],
                                       capture_output=True, text=True)
            
            # 清理临时文件
            subprocess.run(['adb', 'shell', 'rm', '/sdcard/temp_messages.json'], capture_output=True)
            os.remove(temp_file)
            
            if move_result.returncode == 0:
                print(f"\n✅ 已将shared_messages.json更新为格式化格式")
                print(f"   - 总消息数: {len(formatted_messages)}")
                return True
            else:
                print(f"移动文件到应用目录失败: {move_result.stderr}")
                return False
        else:
            print(f"推送文件到设备失败: {push_result.stderr}")
            os.remove(temp_file)
            return False
        
    except Exception as e:
        print(f"格式化并更新消息失败: {e}")
        return False

def MessageSendCheck(receiverId, senderId, message_content):
    # 从设备获取用户新发送的消息文件
    # run里面的com.example.GaoDe是APP名字；files/shared_messages.json是用户消息文件路径
    try:
        # 尝试从设备获取用户发送的消息文件
        try:
            result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 'cat', 'files/shared_messages.json'],
                                   stdout=subprocess.PIPE, stderr=subprocess.PIPE, encoding='utf-8', errors='ignore')
            user_messages = []
            if result.returncode == 0 and result.stdout and result.stdout.strip():
                # 解析用户发送的消息
                try:
                    user_messages = json.loads(result.stdout)
                    print(f"从设备获取到 {len(user_messages)} 条用户消息")
                except json.JSONDecodeError as je:
                    print(f"用户消息文件格式错误: {je}")
            else:
                print("未找到用户消息文件或文件为空")
                if result.stderr:
                    print(f"错误信息: {result.stderr}")
        except Exception as e:
            print(f"获取用户消息失败: {e}")
            user_messages = []
        
        # 同时获取原始消息文件用于对比
        try:
            original_result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 'cat', 'assets/data/messages.json'],
                                           stdout=subprocess.PIPE, stderr=subprocess.PIPE, encoding='utf-8', errors='ignore')
            original_messages = []
            if original_result.returncode == 0 and original_result.stdout and original_result.stdout.strip():
                try:
                    original_messages = json.loads(original_result.stdout)
                    print(f"从设备获取到 {len(original_messages)} 条原始消息")
                except json.JSONDecodeError as je:
                    print(f"原始消息文件格式错误: {je}")
            else:
                print("未找到原始消息文件或文件为空")
                if original_result.stderr:
                    print(f"错误信息: {original_result.stderr}")
        except Exception as e:
            print(f"获取原始消息失败: {e}")
            original_messages = []
        
        # 如果ADB命令都失败，使用本地测试文件
        if not user_messages and not original_messages:
            print("ADB命令失败，使用本地测试文件...")
            try:
                local_file_path = os.path.join(os.path.dirname(__file__), '..', 'app', 'src', 'main', 'assets', 'data', 'messages.json')
                with open(local_file_path, 'r', encoding='utf-8') as f:
                    original_messages = json.load(f)
                print(f"从本地文件加载了 {len(original_messages)} 条消息")
            except Exception as e:
                print(f"加载本地文件失败: {e}")
                return False

    except Exception as e:
        print(f"获取消息文件失败: {e}")
        return False

    # 判断给定的信息是否存在于聊天记录里面
    try:
        # 检查用户新发送的消息（shared_messages格式）
        user_matching_messages = []
        for msg in user_messages:
            if msg['contactId'] == receiverId and msg['message'] == message_content:
                user_matching_messages.append(msg)
        
        print(f"用户新发送的消息中找到 {len(user_matching_messages)} 条匹配的消息:")
        for msg in user_matching_messages:
            print(f"  - 给 {msg['contactName']} ({msg['contactId']}): {msg['message']} (时间: {msg['timestamp']})")
        
        if user_matching_messages:
            # 按时间戳排序，获取最新的消息
            latest_user_message = max(user_matching_messages, key=lambda x: x['timestamp'])
            print(f"✓ 找到匹配的用户消息: {latest_user_message['message']}")
            
            # 格式化并更新设备上的shared_messages.json
            format_and_update_shared_messages(user_messages)
            return True
        
        # 检查原始消息文件（messages格式）
        original_matching_messages = []
        for msg in original_messages:
            if (msg.get('senderId') == senderId and 
                msg.get('receiverId') == receiverId and 
                msg.get('content') == message_content):
                original_matching_messages.append(msg)
        
        print(f"原始消息中找到 {len(original_matching_messages)} 条匹配的消息:")
        for msg in original_matching_messages:
            print(f"  - {msg['content']} (时间: {msg['timestamp']})")
        
        if original_matching_messages:
            latest_original_message = max(original_matching_messages, key=lambda x: x['timestamp'])
            print(f"✓ 找到匹配的原始消息: {latest_original_message['content']}")
            
            # 格式化并更新设备上的shared_messages.json
            format_and_update_shared_messages(user_messages)
            return True
        
        print(f"✗ 未找到匹配的消息: '{message_content}'")
        print(f"  - 在用户消息中查找: 发送给 {receiverId} 的消息")
        print(f"  - 在原始消息中查找: 从 {senderId} 发送给 {receiverId} 的消息")
        return False
        
    except Exception as e:
        print(f"消息检查失败: {e}")
        return False

if __name__ == "__main__":
    print(MessageSendCheck(
         receiverId='dad',        # 爸爸的id是dad
         senderId='user_001',     # 这是"我"的id
         message_content='吃了吗'  # 发送内容：吃了吗
    )) 