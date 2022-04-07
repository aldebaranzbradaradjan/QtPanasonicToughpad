import QtQuick 2.15
import QtQuick.Window 2.15
import QtQuick.Controls 2.15
import QtQuick.Layouts 1.15
import QtQuick.Controls.Material 2.12

ApplicationWindow {
    visible: true

    Material.theme: Material.Dark
    Material.accent: Material.Purple

    Component.onCompleted: PanasonicToughpad.enableReader()

    ColumnLayout {
        anchors.fill: parent

        Text {
            id: text
            text: ""
            color: "white"
            Layout.alignment: Qt.AlignHCenter
        }

        Button {
            text: "SCAN"
            Layout.alignment: Qt.AlignHCenter
            onPressed: PanasonicToughpad.pressSoftwareTrigger(true)
            onReleased: PanasonicToughpad.pressSoftwareTrigger(false)
        }
    }

    Connections {
        target: PanasonicToughpad

        function onBarcodeReaded() {
            text.text = PanasonicToughpad.data + " "
                        + PanasonicToughpad.device + " "
                        + PanasonicToughpad.symbology
        }

        function onButtonPressed(button, state) {
            if( button === "USER" || button === "SIDE" )
                PanasonicToughpad.pressSoftwareTrigger(state)
        }
    }
}
