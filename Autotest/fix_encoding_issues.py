#!/usr/bin/env python3
"""
Script to fix encoding issues in all eval_*.py files
"""

import os
import glob
import re

def fix_file_encoding(filepath):
    """Fix encoding issues in a single file"""
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Fix uiautomator subprocess calls
        old_pattern = r"subprocess\.run\(\['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'\],\s*capture_output=True, text=True\)"
        new_pattern = "subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], capture_output=True, text=True, encoding='utf-8', errors='ignore')"
        content = re.sub(old_pattern, new_pattern, content)
        
        # Add null check after ui_dump assignment
        old_ui_dump = r"ui_dump = result\.stdout\n\s*# Check"
        new_ui_dump = """ui_dump = result.stdout
            
            if ui_dump is None:
                ui_dump = ""
            
            # Check"""
        content = re.sub(old_ui_dump, new_ui_dump, content)
        
        # Remove Chinese characters from print statements with found_indicators
        old_print = r"print\(f\"SUCCESS: Found .* elements in UI: \{', '\.join\(found_indicators\)\}\"\)"
        new_print = 'print(f"SUCCESS: Found UI elements")'
        content = re.sub(old_print, new_print, content)
        
        # Write back to file
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        
        print(f"Fixed encoding issues in {os.path.basename(filepath)}")
        return True
        
    except Exception as e:
        print(f"Error fixing {filepath}: {e}")
        return False

def main():
    """Fix all eval_*.py files in current directory"""
    script_dir = os.path.dirname(os.path.abspath(__file__))
    pattern = os.path.join(script_dir, 'eval_*.py')
    
    eval_files = glob.glob(pattern)
    
    if not eval_files:
        print("No eval_*.py files found")
        return
    
    print(f"Found {len(eval_files)} eval files to fix:")
    
    success_count = 0
    for filepath in eval_files:
        if fix_file_encoding(filepath):
            success_count += 1
    
    print(f"\nSuccessfully fixed {success_count}/{len(eval_files)} files")

if __name__ == "__main__":
    main()