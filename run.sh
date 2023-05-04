threadCount=6
suites=$1
env=$2

case $1 in
booking)
  suites='booking/BookingSuite.xml'
  ;;
all)
  suites='TestNG.xml'
  ;;
esac

if [[ $suites == 'booking' ]]; then
  threadCount=1
fi

mvn -Dsuites=$suites -DthreadCount=$threadCount -DwaitUntilOrderIsClosed=$waitUntilOrderIsClosed -DprocessOrder=$processOrder test -P $env
