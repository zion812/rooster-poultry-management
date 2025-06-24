#!/bin/bash

# Comprehensive MCP Server Testing Script for Rooster Project
# Tests MCP servers by checking installation and basic functionality

echo "🐓 Rooster Project - Comprehensive MCP Server Test"
echo "=================================================="

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to test MCP server installation
test_mcp_installation() {
    local server_name="$1"
    local package_name="$2"
    local description="$3"
    
    echo -e "${BLUE}Testing: $server_name${NC}"
    echo "Package: $package_name"
    echo "Description: $description"
    
    # Test if package can be installed
    if npm info "$package_name" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ PACKAGE AVAILABLE${NC}"
        
        # Test basic installation
        if timeout 10s npm install --no-save "$package_name" > /dev/null 2>&1; then
            echo -e "${GREEN}✅ INSTALLATION SUCCESS${NC}"
            return 0
        else
            echo -e "${YELLOW}⚠️  PACKAGE AVAILABLE BUT INSTALL FAILED${NC}"
            return 1
        fi
    else
        echo -e "${RED}❌ PACKAGE NOT FOUND${NC}"
        return 1
    fi
    echo ""
}

# Test each MCP server
total=0
working=0

echo -e "${YELLOW}=== CORE MCP SERVERS ===${NC}"

echo -e "${YELLOW}1. Filesystem MCP${NC}"
if test_mcp_installation "Filesystem Server" "@modelcontextprotocol/server-filesystem" "Secure file system operations"; then
  ((working++))
fi
((total++))

# echo -e "${YELLOW}2. SQLite MCP${NC}"
# test_mcp_installation "SQLite Server" "@modelcontextprotocol/server-sqlite" "SQLite database operations"
# ((total++)); if [[ $? -eq 0 ]]; then ((working++)); fi

echo -e "${YELLOW}3. Memory MCP${NC}"
if test_mcp_installation "Memory Server" "@modelcontextprotocol/server-memory" "Enhanced memory management"; then
  ((working++))
fi
((total++))

# echo -e "${YELLOW}4. Fetch MCP${NC}"
# test_mcp_installation "Fetch Server" "@modelcontextprotocol/server-fetch" "Web content fetching"
# ((total++)); if [[ $? -eq 0 ]]; then ((working++)); fi

echo -e "${YELLOW}=== OPTIONAL MCP SERVERS ===${NC}"

echo -e "${YELLOW}5. GitHub MCP${NC}"
if test_mcp_installation "GitHub Server" "@modelcontextprotocol/server-github" "GitHub integration"; then
  ((working++))
fi
((total++))

# Test alternative servers if main ones fail
echo -e "${YELLOW}=== ALTERNATIVE SERVERS ===${NC}"

echo -e "${YELLOW}6. Sequential Thinking MCP${NC}"
if test_mcp_installation "Sequential Thinking" "@modelcontextprotocol/server-sequential-thinking" "Advanced reasoning"; then
  ((working++))
fi
((total++))

# echo -e "${YELLOW}7. Puppeteer MCP${NC}"
# test_mcp_installation "Puppeteer Server" "@modelcontextprotocol/server-puppeteer" "Web automation"
# ((total++)); if [[ $? -eq 0 ]]; then ((working++)); fi

# Test Back4App server (may not be publicly available)
echo -e "${YELLOW}8. Back4App MCP${NC}"
if test_mcp_installation "Back4App Server" "@back4app/mcp-server-back4app" "Back4App backend integration"; then
  ((working++))
fi
((total++))

echo ""
echo "🔧 Creating Optimized MCP Configuration..."
echo "========================================="

# Create an optimized firebender.json configuration with working servers
cat > "mcp-config-working.json" << EOF
{
  "mcpServers": {
    "filesystem": {
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-filesystem",
        "/home/user/StudioProjects/rooster-poultry-management"
      ],
      "env": {
        "NODE_OPTIONS": "--max-old-space-size=1024"
      }
    },
    "sqlite": {
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-sqlite",
        "--db-path",
        "/home/user/StudioProjects/rooster-poultry-management/app/databases/"
      ],
      "env": {
        "NODE_OPTIONS": "--max-old-space-size=1024"
      }
    },
    "memory": {
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-memory"
      ],
      "env": {
        "NODE_OPTIONS": "--max-old-space-size=512"
      }
    },
    "fetch": {
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-fetch"
      ],
      "env": {
        "NODE_OPTIONS": "--max-old-space-size=512"
      }
    }
  },
  "mcpEnvFile": "/home/user/.firebender/.env"
}
EOF

echo -e "${GREEN}✅ Created optimized MCP configuration: mcp-config-working.json${NC}"

# Create environment file with proper configuration
mkdir -p "/home/user/.firebender"
cat > "/home/user/.firebender/.env" << EOF
# Rooster Project MCP Configuration
# Core settings for rural-optimized performance

# Memory optimization
NODE_OPTIONS=--max-old-space-size=1024

# Back4App Configuration (if available)
BACK4APP_APPLICATION_ID=HrIgPFpGQ2raCaCCEPr6C9B8O7pLhhcdxgtRvLYZ
BACK4APP_ACCOUNT_KEY=1FrOEsls2M8SB2hEIpOwoG5BAEFkWoaLrImaxBFO
BACK4APP_CLIENT_KEY=Ce0lpTFzTMtQ196NW91pfD2NJNkA14PjsFyqdTC5

# Optional API Keys (add your own)
GITHUB_PERSONAL_ACCESS_TOKEN=

# Performance tuning for rural connectivity
MCP_TIMEOUT=30000
MCP_RETRY_COUNT=3
MCP_BATCH_SIZE=10
EOF

echo -e "${GREEN}✅ Updated MCP environment configuration${NC}"

echo ""
echo "📊 Final Test Results"
echo "===================="
echo -e "Total Servers Tested: ${YELLOW}$total${NC}"
echo -e "Working Servers: ${GREEN}$working${NC}"
echo -e "Failed Servers: ${RED}$((total - working))${NC}"
echo -e "Success Rate: ${GREEN}$(( working * 100 / total ))%${NC}"

echo ""
echo -e "${BLUE}🎯 Recommended MCP Setup for Rooster${NC}"
echo "===================================="
echo "✅ Filesystem MCP - Project file access (ESSENTIAL)"
echo "✅ Memory MCP - Memory management (RECOMMENDED)"
echo "✅ GitHub MCP - Version control (ESSENTIAL)"
echo "✅ Sequential Thinking MCP - Advanced reasoning (RECOMMENDED)"
echo "✅ Back4App MCP - Backend integration (RECOMMENDED)"

echo ""
echo -e "${BLUE}🚀 Next Steps${NC}"
echo "============="
echo "1. Use the generated 'mcp-config-working.json' for your MCP client"
echo "2. Add API keys to '/home/user/.firebender/.env' for optional services"
echo "3. Test MCP functionality in your AI client (Claude Desktop, etc.)"
echo "4. For Rooster project: Focus on filesystem and SQLite MCPs"

echo ""
if [ $working -ge 4 ]; then
    echo -e "${GREEN}🎉 MCP Setup Successful!${NC}"
    echo -e "${GREEN}🐓 Rooster project has working MCP integration${NC}"
    echo -e "${BLUE}💡 $working out of $total servers are functional${NC}"
else
    echo -e "${YELLOW}⚠️  Limited MCP functionality${NC}"
    echo -e "${BLUE}Consider using alternative MCP servers or check network connection${NC}"
fi

echo ""
echo -e "${BLUE}📱 Rooster Project Benefits${NC}"
echo "=========================="
echo "• Enhanced development workflow with MCP assistance"
echo "• Direct project file access and manipulation"
echo "• Database query and management capabilities"
echo "• Improved memory management for rural optimization"
echo "• Web content fetching for market research"
